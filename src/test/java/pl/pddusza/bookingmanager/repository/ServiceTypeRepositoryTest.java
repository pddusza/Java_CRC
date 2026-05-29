package pl.pddusza.bookingmanager.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.pddusza.bookingmanager.model.ServiceType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ServiceTypeRepositoryTest {

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM appointments");
        jdbcTemplate.update("DELETE FROM services");
    }

    @Test
    void shouldSaveServiceTypeAndReturnGeneratedId() {
        ServiceType serviceType = new ServiceType(
                null,
                "Initial consultation",
                "First medical consultation",
                30,
                new BigDecimal("150.00"),
                true
        );

        ServiceType savedServiceType = serviceTypeRepository.save(serviceType);

        assertNotNull(savedServiceType.getId());
        assertEquals("Initial consultation", savedServiceType.getName());
        assertEquals("First medical consultation", savedServiceType.getDescription());
        assertEquals(30, savedServiceType.getDurationMinutes());
        assertEquals(0, new BigDecimal("150.00").compareTo(savedServiceType.getPrice()));
        assertTrue(savedServiceType.isActive());
    }

    @Test
    void shouldFindServiceTypeById() {
        ServiceType savedServiceType = serviceTypeRepository.save(new ServiceType(
                null,
                "Control visit",
                "Follow-up appointment",
                20,
                new BigDecimal("100.00"),
                true
        ));

        Optional<ServiceType> foundServiceType = serviceTypeRepository.findById(savedServiceType.getId());

        assertTrue(foundServiceType.isPresent());
        assertEquals("Control visit", foundServiceType.get().getName());
        assertEquals(20, foundServiceType.get().getDurationMinutes());
    }


    @Test
    void shouldFindOnlyActiveServiceTypes() {
        ServiceType activeServiceType = serviceTypeRepository.save(new ServiceType(
                null,
                "Initial consultation",
                "First medical consultation",
                30,
                new BigDecimal("150.00"),
                true
        ));
        serviceTypeRepository.save(new ServiceType(
                null,
                "Archived service",
                "Inactive service",
                15,
                new BigDecimal("50.00"),
                false
        ));

        List<ServiceType> activeServiceTypes = serviceTypeRepository.findActive();

        assertEquals(1, activeServiceTypes.size());
        assertEquals(activeServiceType.getId(), activeServiceTypes.get(0).getId());
        assertTrue(activeServiceTypes.get(0).isActive());
    }

    @Test
    void shouldUpdateServiceType() {
        ServiceType savedServiceType = serviceTypeRepository.save(new ServiceType(
                null,
                "Initial consultation",
                "First medical consultation",
                30,
                new BigDecimal("150.00"),
                true
        ));

        ServiceType updatedServiceType = new ServiceType(
                savedServiceType.getId(),
                "Extended consultation",
                "Longer specialist consultation",
                45,
                new BigDecimal("220.00"),
                false
        );

        boolean updated = serviceTypeRepository.update(updatedServiceType);
        Optional<ServiceType> foundServiceType = serviceTypeRepository.findById(savedServiceType.getId());

        assertTrue(updated);
        assertTrue(foundServiceType.isPresent());
        assertEquals("Extended consultation", foundServiceType.get().getName());
        assertEquals("Longer specialist consultation", foundServiceType.get().getDescription());
        assertEquals(45, foundServiceType.get().getDurationMinutes());
        assertEquals(0, new BigDecimal("220.00").compareTo(foundServiceType.get().getPrice()));
        assertFalse(foundServiceType.get().isActive());
    }

    @Test
    void shouldReturnFalseWhenUpdatingMissingServiceType() {
        ServiceType missingServiceType = new ServiceType(
                999L,
                "Missing service",
                "Unknown service",
                10,
                new BigDecimal("10.00"),
                true
        );

        boolean updated = serviceTypeRepository.update(missingServiceType);

        assertFalse(updated);
    }
    
    @Test
    void shouldReturnFalseWhenDeletingMissingServiceType() {
        boolean deleted = serviceTypeRepository.deleteById(999L);

        assertFalse(deleted);
    }

    @Test
    void shouldDeleteServiceTypeById() {
        ServiceType savedServiceType = serviceTypeRepository.save(new ServiceType(
                null,
                "Initial consultation",
                "First medical consultation",
                30,
                new BigDecimal("150.00"),
                true
        ));

        boolean deleted = serviceTypeRepository.deleteById(savedServiceType.getId());
        Optional<ServiceType> foundServiceType = serviceTypeRepository.findById(savedServiceType.getId());

        assertTrue(deleted);
        assertTrue(foundServiceType.isEmpty());
    }

    @Test
    void shouldReturnEmptyOptionalWhenServiceTypeDoesNotExist() {
        Optional<ServiceType> foundServiceType = serviceTypeRepository.findById(999L);

        assertTrue(foundServiceType.isEmpty());
    }

}