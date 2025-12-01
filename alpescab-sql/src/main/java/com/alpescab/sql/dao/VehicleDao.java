package com.alpescab.sql.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VehicleDao {
    private final JdbcTemplate jdbc;

    public Long createVehicle(Long driverId, String type, String brand, String model,
                              String color, String plate, String plateCity, int capacity, String level) {
        String sql = "INSERT INTO VEHICLE (DRIVER_ID, TYPE, BRAND, MODEL, COLOR, PLATE, PLATE_CITY, CAPACITY, LEVEL) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        jdbc.update(sql, driverId, type, brand, model, color, plate, plateCity, capacity, level);
        return jdbc.queryForObject("SELECT MAX(ID) FROM VEHICLE", Long.class);
    }

    public Long findVehicleForDriverAndLevel(Long driverId, String level) {
        String sql = "SELECT ID FROM VEHICLE WHERE DRIVER_ID = ? AND LEVEL = ? FETCH FIRST 1 ROWS ONLY";
        return jdbc.query(sql, ps -> {
            ps.setLong(1, driverId);
            ps.setString(2, level);
        }, rs -> rs.next() ? rs.getLong("ID") : null);
    }
}
