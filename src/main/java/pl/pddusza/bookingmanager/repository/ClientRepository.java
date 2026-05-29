package pl.pddusza.bookingmanager.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pl.pddusza.bookingmanager.model.Client;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ClientRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Client> clientRowMapper = (resultSet, rowNumber) -> new Client(
            resultSet.getLong("id"),
            resultSet.getString("first_name"),
            resultSet.getString("last_name"),
            resultSet.getString("email"),
            resultSet.getString("phone_number"),
            resultSet.getObject("created_at", LocalDateTime.class)
    );

    public ClientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Client save(Client client) {
        String sql = """
                INSERT INTO clients (first_name, last_name, email, phone_number)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, client.getFirstName());
            statement.setString(2, client.getLastName());
            statement.setString(3, client.getEmail());
            statement.setString(4, client.getPhoneNumber());
            return statement;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return findById(generatedId)
                .orElseThrow(() -> new IllegalStateException("Saved client could not be loaded from database."));
    }

    public Optional<Client> findById(Long id) {
        String sql = """
                SELECT id, first_name, last_name, email, phone_number, created_at
                FROM clients
                WHERE id = ?
                """;

        List<Client> clients = jdbcTemplate.query(sql, clientRowMapper, id);
        return clients.stream().findFirst();
    }

    public List<Client> findAll() {
        String sql = """
                SELECT id, first_name, last_name, email, phone_number, created_at
                FROM clients
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, clientRowMapper);
    }

    public boolean update(Client client) {
        String sql = """
                UPDATE clients
                SET first_name = ?,
                    last_name = ?,
                    email = ?,
                    phone_number = ?
                WHERE id = ?
                """;

        int updatedRows = jdbcTemplate.update(
                sql,
                client.getFirstName(),
                client.getLastName(),
                client.getEmail(),
                client.getPhoneNumber(),
                client.getId()
        );

        return updatedRows == 1;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        int deletedRows = jdbcTemplate.update(sql, id);
        return deletedRows == 1;
    }
}