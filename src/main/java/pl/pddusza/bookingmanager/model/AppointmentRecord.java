package pl.pddusza.bookingmanager.model;

import java.time.LocalDateTime;

public class AppointmentRecord {

    private final Long id;
    private final Long clientId;
    private final Long doctorId;
    private final Long serviceTypeId;
    private final LocalDateTime appointmentStart;
    private final LocalDateTime appointmentEnd;
    private final AppointmentStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime cancelledAt;

    public AppointmentRecord(Long id,
                             Long clientId,
                             Long doctorId,
                             Long serviceTypeId,
                             LocalDateTime appointmentStart,
                             LocalDateTime appointmentEnd,
                             AppointmentStatus status,
                             LocalDateTime createdAt,
                             LocalDateTime cancelledAt) {
        this.id = id;
        this.clientId = clientId;
        this.doctorId = doctorId;
        this.serviceTypeId = serviceTypeId;
        this.appointmentStart = appointmentStart;
        this.appointmentEnd = appointmentEnd;
        this.status = status;
        this.createdAt = createdAt;
        this.cancelledAt = cancelledAt;
    }

    public Long getId() {
        return id;
    }

    public Long getClientId() {
        return clientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public Long getServiceTypeId() {
        return serviceTypeId;
    }

    public LocalDateTime getAppointmentStart() {
        return appointmentStart;
    }

    public LocalDateTime getAppointmentEnd() {
        return appointmentEnd;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
}