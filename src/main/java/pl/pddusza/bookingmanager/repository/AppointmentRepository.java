package pl.pddusza.bookingmanager.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pl.pddusza.bookingmanager.model.AppointmentRecord;
import pl.pddusza.bookingmanager.model.AppointmentStatus;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class AppointmentRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<AppointmentRecord> appointmentRecordRowMapper = (resultSet, rowNumber) ->
            new AppointmentRecord(
                    resultSet.getLong("id"),
                    resultSet.getLong("client_id"),
                    resultSet.getLong("doctor_id"),
                    resultSet.getLong("service_id"),
                    resultSet.getObject("appointment_start", LocalDateTime.class),
                    resultSet.getObject("appointment_end", LocalDateTime.class),
                    AppointmentStatus.valueOf(resultSet.getString("status")),
                    resultSet.getObject("created_at", LocalDateTime.class),
                    resultSet.getObject("cancelled_at", LocalDateTime.class)
            );

    public AppointmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AppointmentRecord save(Long clientId,
                                  Long doctorId,
                                  Long serviceTypeId,
                                  LocalDateTime appointmentStart,
                                  LocalDateTime appointmentEnd,
                                  AppointmentStatus status) {
        String sql = """
                INSERT INTO appointments (client_id, doctor_id, service_id, appointment_start, appointment_end, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setLong(1, clientId);
            statement.setLong(2, doctorId);
            statement.setLong(3, serviceTypeId);
            statement.setTimestamp(4, Timestamp.valueOf(appointmentStart));
            statement.setTimestamp(5, Timestamp.valueOf(appointmentEnd));
            statement.setString(6, status.name());
            return statement;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return findById(generatedId)
                .orElseThrow(() -> new IllegalStateException("Saved appointment could not be loaded from database."));
    }

    public Optional<AppointmentRecord> findById(Long id) {
        String sql = """
                SELECT id, client_id, doctor_id, service_id, appointment_start, appointment_end,
                       status, created_at, cancelled_at
                FROM appointments
                WHERE id = ?
                """;

        List<AppointmentRecord> appointments = jdbcTemplate.query(sql, appointmentRecordRowMapper, id);
        return appointments.stream().findFirst();
    }

    public List<AppointmentRecord> findByDoctorId(Long doctorId) {
        String sql = """
                SELECT id, client_id, doctor_id, service_id, appointment_start, appointment_end,
                       status, created_at, cancelled_at
                FROM appointments
                WHERE doctor_id = ?
                ORDER BY appointment_start, id
                """;

        return jdbcTemplate.query(sql, appointmentRecordRowMapper, doctorId);
    }

    public boolean existsOverlappingAppointment(Long doctorId,
                                                LocalDateTime appointmentStart,
                                                LocalDateTime appointmentEnd) {
        String sql = """
                SELECT COUNT(*)
                FROM appointments
                WHERE doctor_id = ?
                  AND status = 'PLANNED'
                  AND appointment_start < ?
                  AND appointment_end > ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                doctorId,
                Timestamp.valueOf(appointmentEnd),
                Timestamp.valueOf(appointmentStart)
        );

        return count != null && count > 0;
    }
}