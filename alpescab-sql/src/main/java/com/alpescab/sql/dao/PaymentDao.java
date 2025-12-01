package com.alpescab.sql.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentDao {
    private final JdbcTemplate jdbc;

    public boolean hasActivePaymentMethod(Long serviceUserId) {
        String sql = "SELECT COUNT(*) FROM PAYMENT_METHOD WHERE SERVICE_USER_ID = ? AND ACTIVE = 'Y'";
        Integer count = jdbc.queryForObject(sql, Integer.class, serviceUserId);
        return count != null && count > 0;
    }
}
