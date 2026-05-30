package pl.pddusza.bookingmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.pddusza.bookingmanager.exception.AppointmentConflictException;
import pl.pddusza.bookingmanager.model.AppointmentRecord;
import pl.pddusza.bookingmanager.model.AppointmentStatus;
import pl.pddusza.bookingmanager.model.Client;
import pl.pddusza.bookingmanager.model.Doctor;
import pl.pddusza.bookingmanager.model.ServiceType;
import pl.pddusza.bookingmanager.repository.ClientRepository;
import pl.pddusza.bookingmanager.repository.DoctorRepository;
import pl.pddusza.bookingmanager.repository.ServiceTypeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class AppointmentServiceTest {

    @Autowired
    private AppointmentService appointmentService;

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
    void shouldCreateAppointment() {
        Client client = saveClient("anna.service@example.com");
        Doctor doctor = saveDoctor("jan.service@example.com");
        ServiceType serviceType = saveServiceType("Initial consultation service test", 30);
        LocalDateTime start = LocalDateTime.of(2026, 6, 2, 9, 0);

        AppointmentRecord appointment = appointmentService.createAppointment(
                client.getId(),
                doctor.getId(),
                serviceType.getId(),
                start
        );

        assertNotNull(appointment.getId());
        assertEquals(AppointmentStatus.PLANNED, appointment.getStatus());
        assertEquals(start, appointment.getAppointmentStart());
        assertEquals(start.plusMinutes(30), appointment.getAppointmentEnd());
        assertEquals(client.getId(), appointment.getClientId());
        assertEquals(doctor.getId(), appointment.getDoctorId());
        assertEquals(serviceType.getId(), appointment.getServiceTypeId());
    }

    @Test
    void shouldRejectOverlappingAppointment() {
        Client firstClient = saveClient("first.client@example.com");
        Client secondClient = saveClient("second.client@example.com");
        Doctor doctor = saveDoctor("conflict.doctor@example.com");
        ServiceType serviceType = saveServiceType("Conflict consultation", 30);
        LocalDateTime start = LocalDateTime.of(2026, 6, 2, 10, 0);

        appointmentService.createAppointment(firstClient.getId(), doctor.getId(), serviceType.getId(), start);

        assertThrows(AppointmentConflictException.class, () -> appointmentService.createAppointment(
                secondClient.getId(),
                doctor.getId(),
                serviceType.getId(),
                start.plusMinutes(15)
        ));
    }

    @Test
    void shouldAllowSameTimeAppointmentForDifferentDoctor() {
        Client firstClient = saveClient("different.first.client@example.com");
        Client secondClient = saveClient("different.second.client@example.com");
        Doctor firstDoctor = saveDoctor("different.first.doctor@example.com");
        Doctor secondDoctor = saveDoctor("different.second.doctor@example.com");
        ServiceType serviceType = saveServiceType("Different doctor consultation", 30);
        LocalDateTime start = LocalDateTime.of(2026, 6, 2, 14, 0);

        appointmentService.createAppointment(firstClient.getId(), firstDoctor.getId(), serviceType.getId(), start);

        AppointmentRecord secondAppointment = assertDoesNotThrow(() -> appointmentService.createAppointment(
                secondClient.getId(),
                secondDoctor.getId(),
                serviceType.getId(),
                start
        ));

        assertEquals(secondDoctor.getId(), secondAppointment.getDoctorId());
        assertEquals(start, secondAppointment.getAppointmentStart());
    }

    @Test
    void shouldAllowAppointmentStartingWhenPreviousOneEnds() {
        Client firstClient = saveClient("edge.first.client@example.com");
        Client secondClient = saveClient("edge.second.client@example.com");
        Doctor doctor = saveDoctor("edge.doctor@example.com");
        ServiceType serviceType = saveServiceType("Edge consultation A", 30);
        LocalDateTime start = LocalDateTime.of(2026, 6, 2, 11, 0);

        appointmentService.createAppointment(firstClient.getId(), doctor.getId(), serviceType.getId(), start);

        AppointmentRecord nextAppointment = assertDoesNotThrow(() -> appointmentService.createAppointment(
                secondClient.getId(),
                doctor.getId(),
                serviceType.getId(),
                start.plusMinutes(30)
        ));

        assertEquals(start.plusMinutes(30), nextAppointment.getAppointmentStart());
        assertEquals(start.plusMinutes(60), nextAppointment.getAppointmentEnd());
    }

    @Test
    void shouldAllowAppointmentEndingWhenNextOneStarts() {
        Client firstClient = saveClient("edge.third.client@example.com");
        Client secondClient = saveClient("edge.fourth.client@example.com");
        Doctor doctor = saveDoctor("edge.second.doctor@example.com");
        ServiceType serviceType = saveServiceType("Edge consultation B", 30);
        LocalDateTime laterStart = LocalDateTime.of(2026, 6, 2, 12, 30);

        appointmentService.createAppointment(firstClient.getId(), doctor.getId(), serviceType.getId(), laterStart);

        AppointmentRecord previousAppointment = assertDoesNotThrow(() -> appointmentService.createAppointment(
                secondClient.getId(),
                doctor.getId(),
                serviceType.getId(),
                laterStart.minusMinutes(30)
        ));

        assertEquals(laterStart.minusMinutes(30), previousAppointment.getAppointmentStart());
        assertEquals(laterStart, previousAppointment.getAppointmentEnd());
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
}
