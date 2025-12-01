package com.alpescab.sql.service;

import com.alpescab.sql.dao.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionalAlpescabService {

    private final CityDao cityDao;
    private final ServiceUserDao serviceUserDao;
    private final DriverDao driverDao;
    private final VehicleDao vehicleDao;
    private final AvailabilityDao availabilityDao;
    private final TripDao tripDao;
    private final PaymentDao paymentDao;
    private final ReviewDao reviewDao;

    @Transactional
    public Long registerCity(String name) {
        return cityDao.createCity(name);
    }

    @Transactional
    public Long registerServiceUser(String name, String email, String phone, String nationalId) {
        return serviceUserDao.createUser(name, email, phone, nationalId);
    }

    @Transactional
    public Long registerDriver(String name, String email, String phone, String nationalId) {
        return driverDao.createDriver(name, email, phone, nationalId);
    }

    @Transactional
    public Long registerVehicle(Long driverId, String type, String brand, String model,
                                String color, String plate, String plateCity, int capacity, String level) {
        return vehicleDao.createVehicle(driverId, type, brand, model, color, plate, plateCity, capacity, level);
    }

    @Transactional
    public Long registerAvailability(Long driverId, Long vehicleId, String serviceType,
                                     String dayOfWeek, String startTime, String endTime) {
        return availabilityDao.createAvailability(driverId, vehicleId, serviceType, dayOfWeek, startTime, endTime);
    }

    @Transactional
    public void updateAvailability(Long id, String dayOfWeek, String startTime, String endTime) {
        availabilityDao.updateAvailability(id, dayOfWeek, startTime, endTime);
    }

    @Transactional
    public Long requestService(Long serviceUserId,
                               Long startPointId,
                               Long endPointId,
                               String serviceType,
                               String level,
                               double distanceKm,
                               double pricePerKm) {

        if (!paymentDao.hasActivePaymentMethod(serviceUserId)) {
            throw new IllegalStateException("User has no active payment method");
        }

        Long driverId = availabilityDao.findAvailableDriver(serviceType, level);
        if (driverId == null) {
            throw new IllegalStateException("No driver available");
        }

        Long vehicleId = vehicleDao.findVehicleForDriverAndLevel(driverId, level);
        if (vehicleId == null) {
            throw new IllegalStateException("No vehicle with given level");
        }

        availabilityDao.markDriverBusy(driverId);

        double price = distanceKm * pricePerKm;
        Timestamp startTime = Timestamp.from(Instant.now());

        Long tripId = tripDao.createTrip(serviceUserId, driverId, vehicleId,
                serviceType, level, startPointId, endPointId, distanceKm, price, startTime);

        return tripId;
    }

    @Transactional
    public void finishTrip(Long tripId, double realDistanceKm) {
        Timestamp end = Timestamp.from(Instant.now());
        tripDao.finishTrip(tripId, realDistanceKm, end);
        Long driverId = tripDao.getDriverIdForTrip(tripId);
        availabilityDao.markDriverAvailable(driverId);
    }

    @Transactional
    public Long addReview(Long tripId,
                          Long authorUserId,
                          Long targetUserId,
                          int rating,
                          String comment) {

        Long id = reviewDao.createReview(tripId, authorUserId, targetUserId, rating, comment,
                Timestamp.from(Instant.now()));

        Map<String, Object> stats = reviewDao.getStatsForUser(targetUserId);
        double avg = ((Number) stats.get("AVG_RATING")).doubleValue();
        long cnt = ((Number) stats.get("CNT")).longValue();

        driverDao.updateRating(targetUserId, avg, cnt);
        return id;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TripDao.TripDto> getUserTripsReadCommitted(Long userId) throws InterruptedException {
        List<TripDao.TripDto> before = tripDao.findTripsByUser(userId);
        Thread.sleep(30_000);
        List<TripDao.TripDto> after = tripDao.findTripsByUser(userId);
        return after;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<TripDao.TripDto> getUserTripsSerializable(Long userId) throws InterruptedException {
        List<TripDao.TripDto> before = tripDao.findTripsByUser(userId);
        Thread.sleep(30_000);
        List<TripDao.TripDto> after = tripDao.findTripsByUser(userId);
        return after;
    }

    public List<Map<String, Object>> getTop20Drivers() {
        String sql = "SELECT d.ID, d.NAME, COUNT(t.ID) AS TRIPS_COUNT " +
                "FROM DRIVER d JOIN TRIP t ON t.DRIVER_ID = d.ID " +
                "GROUP BY d.ID, d.NAME " +
                "ORDER BY TRIPS_COUNT DESC FETCH FIRST 20 ROWS ONLY";
        return tripDao.getJdbc().queryForList(sql);
    }
}
