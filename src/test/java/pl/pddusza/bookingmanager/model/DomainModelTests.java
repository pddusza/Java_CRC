package pl.pddusza.bookingmanager.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class DomainModelTests {

    @Test
    void shouldCreateClient() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 4, 18, 10, 30);

        Client client = new Client(
                1L,
                "Anna",
                "Nowak",
                "anna.nowak@example.com",
                "500600700",
                createdAt
        );

        assertEquals("Anna", client.getFirstName());
        assertEquals("Nowak", client.getLastName());
        assertEquals("anna.nowak@example.com", client.getEmail());
    }

    @Test
    void shouldCreateDoctor() {
        Doctor doctor = new Doctor(
                1L,
                "Jan",
                "Kowalski",
                "cardiology",
                "jan.kowalski@example.com",
                true
        );

        assertEquals("Jan", doctor.getFirstName());
        assertEquals("cardiology", doctor.getSpecialization());
    }

    @Test
    void shouldCreateServiceType() {
        ServiceType serviceType = new ServiceType(
                1L,
                "Medical consultation",
                "Basic doctor consultation",
                30,
                new BigDecimal("150.00"),
                true
        );

        assertEquals("Medical consultation", serviceType.getName());
        assertEquals(30, serviceType.getDurationMinutes());
        assertEquals(new BigDecimal("150.00"), serviceType.getPrice());
    }

    @Test
        void shouldCreateAppointmentWithClientDoctorAndService() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 4, 18, 9, 0);
        LocalDateTime start = LocalDateTime.of(2026, 4, 20, 12, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 20, 12, 30);

        Client client = new Client(
                1L,
                "Anna",
                "Nowak",
                "anna.nowak@example.com",
                "500600700",
                createdAt
        );

        Doctor doctor = new Doctor(
                1L,
                "Jan",
                "Kowalski",
                "cardiology",
                "jan.kowalski@example.com",
                true
        );

        ServiceType serviceType = new ServiceType(
                1L,
                "Medical consultation",
                "Basic doctor consultation",
                30,
                new BigDecimal("150.00"),
                true
        );

        Appointment appointment = new Appointment(
                1L,
                client,
                doctor,
                serviceType,
                start,
                end,
                AppointmentStatus.PLANNED,
                createdAt,
                null
        );

        assertSame(client, appointment.getClient());
        assertSame(doctor, appointment.getDoctor());
        assertSame(serviceType, appointment.getServiceType());
        assertEquals(AppointmentStatus.PLANNED, appointment.getStatus());
        assertEquals(start, appointment.getAppointmentStart());
        assertEquals(end, appointment.getAppointmentEnd());
    }

    @Test
    void shouldAllowChangingDoctorActiveStatus() {
        Doctor doctor = new Doctor(
                1L,
                "Jan",
                "Kowalski",
                "cardiology",
                "jan.kowalski@example.com",
                true
        );

        doctor.setActive(false);

        assertFalse(doctor.isActive());
    }
}