package pl.pddusza.bookingmanager.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pl.pddusza.bookingmanager.model.ServiceType;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ServiceTypeRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<ServiceType> serviceTypeRowMapper = (resultSet, rowNumber) -> new ServiceType(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("description"),
            resultSet.getInt("duration_minutes"),
            resultSet.getBigDecimal("price"),
            resultSet.getBoolean("active")
    );

    public ServiceTypeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ServiceType save(ServiceType serviceType) {
        String sql = """
                INSERT INTO services (name, description, duration_minutes, price, active)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, serviceType.getName());
            statement.setString(2, serviceType.getDescription());
            statement.setInt(3, serviceType.getDurationMinutes());
            statement.setBigDecimal(4, serviceType.getPrice());
            statement.setBoolean(5, serviceType.isActive());
            return statement;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return findById(generatedId)
                .orElseThrow(() -> new IllegalStateException("Saved service type could not be loaded from database."));
    }

    public Optional<ServiceType> findById(Long id) {
        String sql = """
                SELECT id, name, description, duration_minutes, price, active
                FROM services
                WHERE id = ?
                """;

        List<ServiceType> serviceTypes = jdbcTemplate.query(sql, serviceTypeRowMapper, id);
        return serviceTypes.stream().findFirst();
    }

    public List<ServiceType> findAll() {
        String sql = """
                SELECT id, name, description, duration_minutes, price, active
                FROM services
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, serviceTypeRowMapper);
    }

    public List<ServiceType> findActive() {
        String sql = """
                SELECT id, name, description, duration_minutes, price, active
                FROM services
                WHERE active = TRUE
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, serviceTypeRowMapper);
    }

    public boolean update(ServiceType serviceType) {
        String sql = """
                UPDATE services
                SET name = ?,
                    description = ?,
                    duration_minutes = ?,
                    price = ?,
                    active = ?
                WHERE id = ?
                """;

        int updatedRows = jdbcTemplate.update(
                sql,
                serviceType.getName(),
                serviceType.getDescription(),
                serviceType.getDurationMinutes(),
                serviceType.getPrice(),
                serviceType.isActive(),
                serviceType.getId()
        );

        return updatedRows == 1;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM services WHERE id = ?";
        int deletedRows = jdbcTemplate.update(sql, id);
        return deletedRows == 1;
    }
}