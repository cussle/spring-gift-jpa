package gift.repository;

import gift.model.Wish;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class WishRepository implements BaseRepository<Wish, Long> {
    private final JdbcTemplate jdbcTemplate;

    public WishRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Wish> wishRowMapper = (rs, rowNum) -> new Wish(
        rs.getLong("id"),
        rs.getLong("member_id"),
        rs.getLong("product_id")
    );

    @Override
    public Optional<Wish> findById(Long id) {
        List<Wish> results = jdbcTemplate.query("SELECT * FROM wishlist WHERE id = ?", wishRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Wish> findAll() {
        return jdbcTemplate.query("SELECT * FROM wishlist", wishRowMapper);
    }

    public List<Wish> findAllByMemberId(Long memberId) {
        return jdbcTemplate.query("SELECT * FROM wishlist WHERE member_id = ?", wishRowMapper, memberId);
    }

    @Override
    public Wish create(Wish wish) {
        jdbcTemplate.update("INSERT INTO wishlist (member_id, product_id) VALUES (?, ?)",
            wish.getMemberId(), wish.getProductId());
        return wish;
    }

    @Override
    public Wish update(Wish wish) {
        jdbcTemplate.update("UPDATE wishlist SET member_id = ?, product_id = ? WHERE id = ?",
            wish.getMemberId(), wish.getProductId(), wish.getId());
        return wish;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM wishlist WHERE id = ?", id);
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM wishlist WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByMemberIdAndProductId(Long memberId, Long productId) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM wishlist WHERE member_id = ? AND product_id = ?",
            Integer.class, memberId, productId);
        return count != null && count > 0;
    }
}