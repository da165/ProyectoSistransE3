package com.alpescab.sql.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReviewDao {
    private final JdbcTemplate jdbc;

    public Long createReview(Long tripId, Long authorUserId, Long targetUserId,
                             int rating, String comment, Timestamp createdAt) {
        String sql = "INSERT INTO REVIEW (TRIP_ID, AUTHOR_USER_ID, TARGET_USER_ID, RATING, COMMENT_TEXT, CREATED_AT) " +
                "VALUES (?,?,?,?,?,?)";
        jdbc.update(sql, tripId, authorUserId, targetUserId, rating, comment, createdAt);
        return jdbc.queryForObject("SELECT MAX(ID) FROM REVIEW", Long.class);
    }

    public Map<String, Object> getStatsForUser(Long targetUserId) {
        String sql = "SELECT AVG(RATING) AS AVG_RATING, COUNT(*) AS CNT FROM REVIEW WHERE TARGET_USER_ID = ?";
        return jdbc.queryForMap(sql, targetUserId);
    }
}
