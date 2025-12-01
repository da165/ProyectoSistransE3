package com.alpescab.sql.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AvailabilityDao {
    private final JdbcTemplate jdbc;

    public Long createAvailability(Long driverId, Long vehicleId, String serviceType,
                                  String dayOfWeek, String startTime, String endTime) {
        String sql = "INSERT INTO AVAILABILITY (DRIVER_ID, VEHICLE_ID, SERVICE_TYPE, DAY_OF_WEEK, START_TIME, END_TIME) " +
                "VALUES (?,?,?,?,?,?)";
        jdbc.update(sql, driverId, vehicleId, serviceType, dayOfWeek, startTime, endTime);
        return jdbc.queryForObject("SELECT MAX(ID) FROM AVAILABILITY", Long.class);
    }

    public void updateAvailability(Long id, String dayOfWeek, String startTime, String endTime) {
        String sql = "UPDATE AVAILABILITY SET DAY_OF_WEEK = ?, START_TIME = ?, END_TIME = ? WHERE ID = ?";
        jdbc.update(sql, dayOfWeek, startTime, endTime, id);
    }

    public Long findAvailableDriver(String serviceType, String level) {
        String sql = "SELECT DISTINCT a.DRIVER_ID FROM AVAILABILITY a " +
                "JOIN VEHICLE v ON a.VEHICLE_ID = v.ID " +
                "WHERE a.SERVICE_TYPE = ? AND v.LEVEL = ? FETCH FIRST 1 ROWS ONLY";
        return jdbc.query(sql, ps -> {
            ps.setString(1, serviceType);
            ps.setString(2, level);
        }, rs -> rs.next() ? rs.getLong("DRIVER_ID") : null);
    }

    public void markDriverBusy(Long driverId) {
        // implementación simple: no-op o podrías actualizar una columna STATUS en DRIVER
    }

    public void markDriverAvailable(Long driverId) {
        // igual que arriba, según tu modelo
    }
}
