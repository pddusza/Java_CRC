package pl.pddusza.bookingmanager.model;

import java.time.LocalDateTime;

public class Appointment {

    private Long id;
    private Client client;
    private Doctor doctor;
    private ServiceType serviceType;
    private LocalDateTime appointmentStart;
    private LocalDateTime appointmentEnd;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;

    public Appointment() {
    }

    public Appointment(Long id, Client client, Doctor doctor, ServiceType serviceType,
                       LocalDateTime appointmentStart, LocalDateTime appointmentEnd,
                       AppointmentStatus status, LocalDateTime createdAt, LocalDateTime cancelledAt) {
        this.id = id;
        this.client = client;
        this.doctor = doctor;
        this.serviceType = serviceType;
        this.appointmentStart = appointmentStart;
        this.appointmentEnd = appointmentEnd;
        this.status = status;
        this.createdAt = createdAt;
        this.cancelledAt = cancelledAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public LocalDateTime getAppointmentStart() {
        return appointmentStart;
    }

    public void setAppointmentStart(LocalDateTime appointmentStart) {
        this.appointmentStart = appointmentStart;
    }

    public LocalDateTime getAppointmentEnd() {
        return appointmentEnd;
    }

    public void setAppointmentEnd(LocalDateTime appointmentEnd) {
        this.appointmentEnd = appointmentEnd;
    }

    public AppointmentStatus getStatus() {
    return status;
    }

    public void setStatus(AppointmentStatus status) {
    this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}