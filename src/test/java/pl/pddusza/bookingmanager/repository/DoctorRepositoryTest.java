package pl.pddusza.bookingmanager.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.pddusza.bookingmanager.model.Doctor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM appointments");
        jdbcTemplate.update("DELETE FROM doctors");
    }

    @Test
    void shouldSaveDoctorAndReturnGeneratedId() {
        Doctor doctor = new Doctor(
                null,
                "Jan",
                "Kowalski",
                "Cardiology",
                "jan.kowalski@example.com",
                true
        );

        Doctor savedDoctor = doctorRepository.save(doctor);

        assertNotNull(savedDoctor.getId());
        assertEquals("Jan", savedDoctor.getFirstName());
        assertEquals("Kowalski", savedDoctor.getLastName());
        assertEquals("Cardiology", savedDoctor.getSpecialization());
        assertEquals("jan.kowalski@example.com", savedDoctor.getEmail());
        assertTrue(savedDoctor.isActive());
    }

    @Test
    void shouldFindDoctorById() {
        Doctor savedDoctor = doctorRepository.save(new Doctor(
                null,
                "Maria",
                "Nowak",
                "Dermatology",
                "maria.nowak@example.com",
                true
        ));

        Optional<Doctor> foundDoctor = doctorRepository.findById(savedDoctor.getId());

        assertTrue(foundDoctor.isPresent());
        assertEquals("Maria", foundDoctor.get().getFirstName());
        assertEquals("Dermatology", foundDoctor.get().getSpecialization());
    }

    @Test
    void shouldFindAllDoctorsOrderedById() {
        Doctor firstDoctor = doctorRepository.save(new Doctor(
                null,
                "Jan",
                "Kowalski",
                "Cardiology",
                "jan.kowalski@example.com",
                true
        ));
        Doctor secondDoctor = doctorRepository.save(new Doctor(
                null,
                "Maria",
                "Nowak",
                "Dermatology",
                "maria.nowak@example.com",
                true
        ));

        List<Doctor> doctors = doctorRepository.findAll();

        assertEquals(2, doctors.size());
        assertEquals(firstDoctor.getId(), doctors.get(0).getId());
        assertEquals(secondDoctor.getId(), doctors.get(1).getId());
    }


    @Test
    void shouldDeleteDoctorById() {
        Doctor savedDoctor = doctorRepository.save(new Doctor(
                null,
                "Jan",
                "Kowalski",
                "Cardiology",
                "jan.kowalski@example.com",
                true
        ));

        boolean deleted = doctorRepository.deleteById(savedDoctor.getId());
        Optional<Doctor> foundDoctor = doctorRepository.findById(savedDoctor.getId());

        assertTrue(deleted);
        assertTrue(foundDoctor.isEmpty());
    }

    @Test
    void shouldFindOnlyActiveDoctors() {
        Doctor activeDoctor = doctorRepository.save(new Doctor(
                null,
                "Jan",
                "Kowalski",
                "Cardiology",
                "jan.kowalski@example.com",
                true
        ));
        doctorRepository.save(new Doctor(
                null,
                "Piotr",
                "Wisniewski",
                "Neurology",
                "piotr.wisniewski@example.com",
                false
        ));

        List<Doctor> activeDoctors = doctorRepository.findActive();

        assertEquals(1, activeDoctors.size());
        assertEquals(activeDoctor.getId(), activeDoctors.get(0).getId());
        assertTrue(activeDoctors.get(0).isActive());
    }

    @Test
    void shouldUpdateDoctor() {
        Doctor savedDoctor = doctorRepository.save(new Doctor(
                null,
                "Jan",
                "Kowalski",
                "Cardiology",
                "jan.kowalski@example.com",
                true
        ));

        Doctor updatedDoctor = new Doctor(
                savedDoctor.getId(),
                "Jan",
                "Kowalczyk",
                "Internal medicine",
                "jan.kowalczyk@example.com",
                false
        );

        boolean updated = doctorRepository.update(updatedDoctor);
        Optional<Doctor> foundDoctor = doctorRepository.findById(savedDoctor.getId());

        assertTrue(updated);
        assertTrue(foundDoctor.isPresent());
        assertEquals("Kowalczyk", foundDoctor.get().getLastName());
        assertEquals("Internal medicine", foundDoctor.get().getSpecialization());
        assertEquals("jan.kowalczyk@example.com", foundDoctor.get().getEmail());
        assertFalse(foundDoctor.get().isActive());
    }



    @Test
    void shouldReturnFalseWhenDeletingMissingDoctor() {
        boolean deleted = doctorRepository.deleteById(999L);

        assertFalse(deleted);
    }


    @Test
    void shouldReturnFalseWhenUpdatingMissingDoctor() {
        Doctor missingDoctor = new Doctor(
                999L,
                "Missing",
                "Doctor",
                "Unknown",
                "missing.doctor@example.com",
                true
        );

        boolean updated = doctorRepository.update(missingDoctor);

        assertFalse(updated);
    }

}