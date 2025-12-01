package com.alpescab.sql.controller;

import com.alpescab.sql.dao.TripDao;
import com.alpescab.sql.service.TransactionalAlpescabService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sql")
public class AlpescabSqlController {

    private final TransactionalAlpescabService service;

    @PostMapping("/cities")
    public Long createCity(@RequestParam String name) {
        return service.registerCity(name);
    }

    @PostMapping("/service-users")
    public Long createServiceUser(@RequestBody ServiceUserDTO dto) {
        return service.registerServiceUser(dto.getName(), dto.getEmail(), dto.getPhone(), dto.getNationalId());
    }

    @PostMapping("/drivers")
    public Long createDriver(@RequestBody DriverDTO dto) {
        return service.registerDriver(dto.getName(), dto.getEmail(), dto.getPhone(), dto.getNationalId());
    }

    @PostMapping("/vehicles")
    public Long createVehicle(@RequestBody VehicleDTO dto) {
        return service.registerVehicle(dto.getDriverId(), dto.getType(), dto.getBrand(), dto.getModel(),
                dto.getColor(), dto.getPlate(), dto.getPlateCity(), dto.getCapacity(), dto.getLevel());
    }

    @PostMapping("/availabilities")
    public Long createAvailability(@RequestBody AvailabilityDTO dto) {
        return service.registerAvailability(dto.getDriverId(), dto.getVehicleId(), dto.getServiceType(),
                dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
    }

    @PostMapping("/trips/request")
    public Long requestTrip(@RequestBody RequestTripDTO dto) {
        return service.requestService(dto.getServiceUserId(), dto.getStartPointId(), dto.getEndPointId(),
                dto.getServiceType(), dto.getLevel(), dto.getDistanceKm(), dto.getPricePerKm());
    }

    @PostMapping("/trips/{id}/finish")
    public void finishTrip(@PathVariable Long id, @RequestParam double distanceKm) {
        service.finishTrip(id, distanceKm);
    }

    @GetMapping("/rfc1/read-committed")
    public List<TripDao.TripDto> rfc1ReadCommitted(@RequestParam Long userId) throws InterruptedException {
        return service.getUserTripsReadCommitted(userId);
    }

    @GetMapping("/rfc1/serializable")
    public List<TripDao.TripDto> rfc1Serializable(@RequestParam Long userId) throws InterruptedException {
        return service.getUserTripsSerializable(userId);
    }

    @GetMapping("/drivers/top")
    public List<Map<String, Object>> topDrivers() {
        return service.getTop20Drivers();
    }

    @Data
    public static class ServiceUserDTO {
        private String name;
        private String email;
        private String phone;
        private String nationalId;
    }

    @Data
    public static class DriverDTO {
        private String name;
        private String email;
        private String phone;
        private String nationalId;
    }

    @Data
    public static class VehicleDTO {
        private Long driverId;
        private String type;
        private String brand;
        private String model;
        private String color;
        private String plate;
        private String plateCity;
        private int capacity;
        private String level;
    }

    @Data
    public static class AvailabilityDTO {
        private Long driverId;
        private Long vehicleId;
        private String serviceType;
        private String dayOfWeek;
        private String startTime;
        private String endTime;
    }

    @Data
    public static class RequestTripDTO {
        private Long serviceUserId;
        private Long startPointId;
        private Long endPointId;
        private String serviceType;
        private String level;
        private double distanceKm;
        private double pricePerKm;
    }
}
