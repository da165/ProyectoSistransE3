package com.alpescab.sql.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CityDao {
    private final JdbcTemplate jdbc;

    public Long createCity(String name) {
        String sql = "INSERT INTO CITY (NAME) VALUES (?)";
        jdbc.update(sql, name);
        return jdbc.queryForObject("SELECT MAX(ID) FROM CITY", Long.class);
    }
}
