package com.alpescab.sql.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceUserDao {
    private final JdbcTemplate jdbc;

    public Long createUser(String name, String email, String phone, String nationalId) {
        String sql = "INSERT INTO SERVICE_USER (NAME, EMAIL, PHONE, NATIONAL_ID) VALUES (?,?,?,?)";
        jdbc.update(sql, name, email, phone, nationalId);
        return jdbc.queryForObject("SELECT MAX(ID) FROM SERVICE_USER", Long.class);
    }
}
