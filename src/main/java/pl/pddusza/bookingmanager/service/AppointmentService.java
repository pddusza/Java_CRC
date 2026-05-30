package pl.pddusza.bookingmanager.service;

import org.springframework.stereotype.Service;
import pl.pddusza.bookingmanager.exception.AppointmentConflictException;
import pl.pddusza.bookingmanager.model.AppointmentRecord;
import pl.pddusza.bookingmanager.model.AppointmentStatus;
import pl.pddusza.bookingmanager.model.ServiceType;
import pl.pddusza.bookingmanager.repository.AppointmentRepository;
import pl.pddusza.bookingmanager.repository.ClientRepository;
import pl.pddusza.bookingmanager.repository.DoctorRepository;
import pl.pddusza.bookingmanager.repository.ServiceTypeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final DoctorRepository doctorRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ClientRepository clientRepository,
                              DoctorRepository doctorRepository,
                              ServiceTypeRepository serviceTypeRepository) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.doctorRepository = doctorRepository;
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public AppointmentRecord createAppointment(Long clientId,
                                               Long doctorId,
                                               Long serviceTypeId,
                                               LocalDateTime appointmentStart) {
        clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));

        ServiceType serviceType = serviceTypeRepository.findById(serviceTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Service type not found: " + serviceTypeId));

        LocalDateTime appointmentEnd = appointmentStart.plusMinutes(serviceType.getDurationMinutes());

        if (appointmentRepository.existsOverlappingAppointment(doctorId, appointmentStart, appointmentEnd)) {
            throw new AppointmentConflictException("Doctor already has an appointment in this time range.");
        }

        return appointmentRepository.save(
                clientId,
                doctorId,
                serviceTypeId,
                appointmentStart,
                appointmentEnd,
                AppointmentStatus.PLANNED
        );
    }


        public Optional<AppointmentRecord> findAppointmentById(Long id) {
                return appointmentRepository.findById(id);
        }

        public List<AppointmentRecord> findAppointmentsByDoctorId(Long doctorId) {
                return appointmentRepository.findByDoctorId(doctorId);
        }
}