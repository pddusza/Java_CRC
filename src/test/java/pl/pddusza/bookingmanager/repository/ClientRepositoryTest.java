package pl.pddusza.bookingmanager.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.pddusza.bookingmanager.model.Client;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM appointments");
        jdbcTemplate.update("DELETE FROM clients");
    }

    @Test
    void shouldSaveClientAndReturnGeneratedId() {
        Client client = new Client(
                null,
                "Anna",
                "Nowak",
                "anna.nowak@example.com",
                "500600700",
                null
        );

        Client savedClient = clientRepository.save(client);

        assertNotNull(savedClient.getId());
        assertEquals("Anna", savedClient.getFirstName());
        assertEquals("Nowak", savedClient.getLastName());
        assertEquals("anna.nowak@example.com", savedClient.getEmail());
        assertEquals("500600700", savedClient.getPhoneNumber());
        assertNotNull(savedClient.getCreatedAt());
    }

    @Test
    void shouldFindClientById() {
        Client savedClient = clientRepository.save(new Client(
                null,
                "Marek",
                "Zielinski",
                "marek.zielinski@example.com",
                "501601701",
                null
        ));

        Optional<Client> foundClient = clientRepository.findById(savedClient.getId());

        assertTrue(foundClient.isPresent());
        assertEquals("Marek", foundClient.get().getFirstName());
        assertEquals("Zielinski", foundClient.get().getLastName());
    }

    @Test
    void shouldReturnEmptyOptionalWhenClientDoesNotExist() {
        Optional<Client> foundClient = clientRepository.findById(999L);

        assertTrue(foundClient.isEmpty());
    }

    @Test
    void shouldFindAllClientsOrderedById() {
        Client firstClient = clientRepository.save(new Client(
                null,
                "Anna",
                "Nowak",
                "anna.nowak@example.com",
                "500600700",
                null
        ));
        Client secondClient = clientRepository.save(new Client(
                null,
                "Marek",
                "Zielinski",
                "marek.zielinski@example.com",
                "501601701",
                null
        ));

        List<Client> clients = clientRepository.findAll();

        assertEquals(2, clients.size());
        assertEquals(firstClient.getId(), clients.get(0).getId());
        assertEquals(secondClient.getId(), clients.get(1).getId());
    }

    @Test
    void shouldUpdateClient() {
        Client savedClient = clientRepository.save(new Client(
                null,
                "Anna",
                "Nowak",
                "anna.nowak@example.com",
                "500600700",
                null
        ));

        Client updatedClient = new Client(
                savedClient.getId(),
                "Anna Maria",
                "Kowalska",
                "anna.kowalska@example.com",
                "600700800",
                savedClient.getCreatedAt()
        );

        boolean updated = clientRepository.update(updatedClient);
        Optional<Client> foundClient = clientRepository.findById(savedClient.getId());

        assertTrue(updated);
        assertTrue(foundClient.isPresent());
        assertEquals("Anna Maria", foundClient.get().getFirstName());
        assertEquals("Kowalska", foundClient.get().getLastName());
        assertEquals("anna.kowalska@example.com", foundClient.get().getEmail());
        assertEquals("600700800", foundClient.get().getPhoneNumber());
    }

    @Test
    void shouldReturnFalseWhenUpdatingMissingClient() {
        Client missingClient = new Client(
                999L,
                "Missing",
                "Client",
                "missing.client@example.com",
                "000000000",
                null
        );

        boolean updated = clientRepository.update(missingClient);

        assertFalse(updated);
    }

    @Test
    void shouldDeleteClientById() {
        Client savedClient = clientRepository.save(new Client(
                null,
                "Anna",
                "Nowak",
                "anna.nowak@example.com",
                "500600700",
                null
        ));

        boolean deleted = clientRepository.deleteById(savedClient.getId());
        Optional<Client> foundClient = clientRepository.findById(savedClient.getId());

        assertTrue(deleted);
        assertTrue(foundClient.isEmpty());
    }

    @Test
    void shouldReturnFalseWhenDeletingMissingClient() {
        boolean deleted = clientRepository.deleteById(999L);

        assertFalse(deleted);
    }
}