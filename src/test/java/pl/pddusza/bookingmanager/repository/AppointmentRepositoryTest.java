package pl.pddusza.bookingmanager.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.pddusza.bookingmanager.model.AppointmentRecord;
import pl.pddusza.bookingmanager.model.AppointmentStatus;
import pl.pddusza.bookingmanager.model.Client;
import pl.pddusza.bookingmanager.model.Doctor;
import pl.pddusza.bookingmanager.model.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM appointments");
        jdbcTemplate.update("DELETE FROM services");
        jdbcTemplate.update("DELETE FROM doctors");
        jdbcTemplate.update("DELETE FROM clients");
    }

    @Test
    void shouldSaveAppointmentAndFindById() {
        Client client = saveClient("anna.appointment@example.com");
        Doctor doctor = saveDoctor("jan.appointment@example.com");
        ServiceType serviceType = saveServiceType("Initial consultation appointment test", 30);
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 10, 0);

        AppointmentRecord savedAppointment = appointmentRepository.save(
                client.getId(),
                doctor.getId(),
                serviceType.getId(),
                start,
                start.plusMinutes(30),
                AppointmentStatus.PLANNED
        );

        Optional<AppointmentRecord> foundAppointment = appointmentRepository.findById(savedAppointment.getId());

        assertNotNull(savedAppointment.getId());
        assertTrue(foundAppointment.isPresent());
        assertEquals(client.getId(), foundAppointment.get().getClientId());
        assertEquals(doctor.getId(), foundAppointment.get().getDoctorId());
        assertEquals(serviceType.getId(), foundAppointment.get().getServiceTypeId());
        assertEquals(AppointmentStatus.PLANNED, foundAppointment.get().getStatus());
    }


    private ServiceType saveServiceType(String name, int durationMinutes) {
        return serviceTypeRepository.save(new ServiceType(
                null,
                name,
                "Medical consultation",
                durationMinutes,
                new BigDecimal("150.00"),
                true
        ));
    }

    private Client saveClient(String email) {
        return clientRepository.save(new Client(
                null,
                "Anna",
                "Nowak",
                email,
                "500600700",
                null
        ));
    }

    private Doctor saveDoctor(String email) {
        return doctorRepository.save(new Doctor(
                null,
                "Jan",
                "Kowalski",
                "Cardiology",
                email,
                true
        ));
    }

}
