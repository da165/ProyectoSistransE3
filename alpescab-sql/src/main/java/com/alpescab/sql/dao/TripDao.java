package com.alpescab.sql.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TripDao {
    private final JdbcTemplate jdbc;

    public Long createTrip(Long serviceUserId, Long driverId, Long vehicleId,
                           String serviceType, String level,
                           Long startPointId, Long endPointId,
                           double distanceKm, double price,
                           Timestamp startTime) {
        String sql = "INSERT INTO TRIP (SERVICE_USER_ID, DRIVER_ID, VEHICLE_ID, SERVICE_TYPE, LEVEL, " +
                "START_POINT_ID, END_POINT_ID, DISTANCE_KM, PRICE, START_TIME, STATUS) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?, 'IN_PROGRESS')";
        jdbc.update(sql, serviceUserId, driverId, vehicleId, serviceType, level,
                startPointId, endPointId, distanceKm, price, startTime);
        return jdbc.queryForObject("SELECT MAX(ID) FROM TRIP", Long.class);
    }

    public void finishTrip(Long tripId, double realDistanceKm, Timestamp endTime) {
        String sql = "UPDATE TRIP SET DISTANCE_KM = ?, END_TIME = ?, STATUS = 'FINISHED' WHERE ID = ?";
        jdbc.update(sql, realDistanceKm, endTime, tripId);
    }

    public Long getDriverIdForTrip(Long tripId) {
        String sql = "SELECT DRIVER_ID FROM TRIP WHERE ID = ?";
        return jdbc.queryForObject(sql, Long.class, tripId);
    }

    public List<TripDto> findTripsByUser(Long userId) {
        String sql = "SELECT t.ID, t.SERVICE_TYPE, t.LEVEL, t.PRICE, t.START_TIME, t.END_TIME, " +
                "d.NAME AS DRIVER_NAME, v.PLATE " +
                "FROM TRIP t " +
                "JOIN DRIVER d ON t.DRIVER_ID = d.ID " +
                "JOIN VEHICLE v ON t.VEHICLE_ID = v.ID " +
                "WHERE t.SERVICE_USER_ID = ?";
        return jdbc.query(sql, (rs, i) -> mapTripDto(rs), userId);
    }

    private TripDto mapTripDto(ResultSet rs) throws SQLException {
        return new TripDto(
                rs.getLong("ID"),
                rs.getString("SERVICE_TYPE"),
                rs.getString("LEVEL"),
                rs.getDouble("PRICE"),
                rs.getTimestamp("START_TIME").toInstant(),
                rs.getTimestamp("END_TIME") == null ? null : rs.getTimestamp("END_TIME").toInstant(),
                rs.getString("DRIVER_NAME"),
                rs.getString("PLATE")
        );
    }

    @Getter
    @AllArgsConstructor
    public static class TripDto {
        private Long id;
        private String serviceType;
        private String level;
        private double price;
        private Instant start;
        private Instant end;
        private String driverName;
        private String plate;
    }

    public JdbcTemplate getJdbc() {
        return jdbc;
    }
}
