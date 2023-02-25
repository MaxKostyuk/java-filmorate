package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO review(content, isPositive, userId, filmId) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"reviewId"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        return getById(keyHolder.getKey().intValue()).get();
    }

    @Override
    public Optional<Review> getById(int id) {
        String sql = "SELECT rv.reviewId, rv.content, rv.userId, rv.filmId, rv.isPositive, " +
                "COUNT(DISTINCT rl.userId) AS count_like, COUNT(DISTINCT rdl.userId) AS count_dislike " +
                "FROM review AS rv LEFT OUTER JOIN review_likes rl ON rv.reviewId = rl.reviewId " +
                "LEFT OUTER JOIN review_dislikes rdl ON rv.reviewId = rdl.reviewId " +
                "WHERE rv.reviewId = ? GROUP BY rv.reviewId";
        return jdbcTemplate.query(sql, new ReviewMapper(), id).stream().findFirst();
    }

    @Override
    public List<Review> getByFilmId(int filmId, int count) {
        String sql = "SELECT rv.reviewId, rv.content, rv.userId, rv.filmId, rv.isPositive, " +
                "COUNT(DISTINCT rl.userId) AS count_like, COUNT(DISTINCT rdl.userId) AS count_dislike " +
                "FROM review AS rv LEFT OUTER JOIN review_likes rl ON rv.reviewId = rl.reviewId " +
                "LEFT OUTER JOIN review_dislikes rdl ON rv.reviewId = rdl.reviewId " +
                "WHERE rv.filmId = ? GROUP BY rv.reviewId";
        return jdbcTemplate.query(sql, new ReviewMapper(), filmId)
                .stream()
                .sorted(new ReviewComparator())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> getAllReviews(int count) {
        String sql = "SELECT rv.reviewId, rv.content, rv.userId, rv.filmId, rv.isPositive, " +
                "COUNT(DISTINCT rl.userId) AS count_like, COUNT(DISTINCT rdl.userId) AS count_dislike " +
                "FROM review AS rv LEFT OUTER JOIN review_likes rl ON rv.reviewId = rl.reviewId " +
                "LEFT OUTER JOIN review_dislikes rdl ON rv.reviewId = rdl.reviewId " +
                "GROUP BY rv.reviewId";
        return jdbcTemplate.query(sql, new ReviewMapper())
                .stream()
                .sorted(new ReviewComparator())
                .limit(count)
                .collect(Collectors.toList());
    }
    @Override
    public Review update(Review review) {
        String sql = "UPDATE review SET content = ?, isPositive = ? WHERE reviewId = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return getById(review.getReviewId())
                .orElseThrow(() -> new ElementNotFoundException("Review with id " + review.getReviewId() + " not found", review.getReviewId()));
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM review WHERE reviewId = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void addLike(int reviewId, int userId) {
        String sql = "INSERT INTO review_likes(reviewId, userId) VALUES (?, ?)";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void deleteLike(int reviewId, int userId) {
        String sql = "DELETE FROM review_likes WHERE reviewId = ? AND userId = ?";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        String sql = "INSERT INTO review_dislikes(reviewId, userId) VALUES (?, ?)";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void deleteDislike(int reviewId, int userId) {
        String sql = "DELETE FROM review_dislikes WHERE reviewId = ? AND userId = ?";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    private class ReviewMapper implements RowMapper<Review> {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            Review review = new Review();

            review.setReviewId(rs.getInt("reviewId"));
            review.setContent(rs.getString("content"));
            review.setFilmId(rs.getInt("filmId"));
            review.setUserId(rs.getInt("userId"));
            review.setIsPositive(rs.getBoolean("isPositive"));

            int useful = rs.getInt("count_like") - rs.getInt("count_dislike");

            review.setUseful(useful);

            return review;
        }
    }

    private class ReviewComparator implements Comparator<Review> {
        @Override
        public int compare(Review o1, Review o2) {
            int usefulness = o2.getUseful() - o1.getUseful();
            return usefulness != 0 ? usefulness : (o1.getReviewId() - o2.getReviewId());
        }
    }

}
