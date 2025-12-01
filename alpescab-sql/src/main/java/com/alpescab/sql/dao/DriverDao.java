package com.alpescab.sql.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DriverDao {
    private final JdbcTemplate jdbc;

    public Long createDriver(String name, String email, String phone, String nationalId) {
        String sql = "INSERT INTO DRIVER (NAME, EMAIL, PHONE, NATIONAL_ID, RATING_AVG, RATING_CNT) VALUES (?,?,?,?,0,0)";
        jdbc.update(sql, name, email, phone, nationalId);
        return jdbc.queryForObject("SELECT MAX(ID) FROM DRIVER", Long.class);
    }

    public void updateRating(Long driverId, double avg, long count) {
        String sql = "UPDATE DRIVER SET RATING_AVG = ?, RATING_CNT = ? WHERE ID = ?";
        jdbc.update(sql, avg, count, driverId);
    }
}
