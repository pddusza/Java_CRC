package pl.pddusza.bookingmanager.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pl.pddusza.bookingmanager.model.Doctor;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class DoctorRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Doctor> doctorRowMapper = (resultSet, rowNumber) -> new Doctor(
            resultSet.getLong("id"),
            resultSet.getString("first_name"),
            resultSet.getString("last_name"),
            resultSet.getString("specialization"),
            resultSet.getString("email"),
            resultSet.getBoolean("active")
    );

    public DoctorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Doctor save(Doctor doctor) {
        String sql = """
                INSERT INTO doctors (first_name, last_name, specialization, email, active)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, doctor.getFirstName());
            statement.setString(2, doctor.getLastName());
            statement.setString(3, doctor.getSpecialization());
            statement.setString(4, doctor.getEmail());
            statement.setBoolean(5, doctor.isActive());
            return statement;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return findById(generatedId)
                .orElseThrow(() -> new IllegalStateException("Saved doctor could not be loaded from database."));
    }

    public Optional<Doctor> findById(Long id) {
        String sql = """
                SELECT id, first_name, last_name, specialization, email, active
                FROM doctors
                WHERE id = ?
                """;

        List<Doctor> doctors = jdbcTemplate.query(sql, doctorRowMapper, id);
        return doctors.stream().findFirst();
    }

    public List<Doctor> findAll() {
        String sql = """
                SELECT id, first_name, last_name, specialization, email, active
                FROM doctors
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, doctorRowMapper);
    }

    public List<Doctor> findActive() {
        String sql = """
                SELECT id, first_name, last_name, specialization, email, active
                FROM doctors
                WHERE active = TRUE
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, doctorRowMapper);
    }

    public boolean update(Doctor doctor) {
        String sql = """
                UPDATE doctors
                SET first_name = ?,
                    last_name = ?,
                    specialization = ?,
                    email = ?,
                    active = ?
                WHERE id = ?
                """;

        int updatedRows = jdbcTemplate.update(
                sql,
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialization(),
                doctor.getEmail(),
                doctor.isActive(),
                doctor.getId()
        );

        return updatedRows == 1;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        int deletedRows = jdbcTemplate.update(sql, id);
        return deletedRows == 1;
    }
}