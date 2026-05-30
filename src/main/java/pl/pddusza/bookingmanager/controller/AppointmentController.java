package pl.pddusza.bookingmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pddusza.bookingmanager.model.AppointmentRecord;
import pl.pddusza.bookingmanager.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentRecord> createAppointment(@RequestParam Long clientId,
                                                            @RequestParam Long doctorId,
                                                            @RequestParam Long serviceTypeId,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                            LocalDateTime appointmentStart) {
        AppointmentRecord appointment = appointmentService.createAppointment(
                clientId,
                doctorId,
                serviceTypeId,
                appointmentStart
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentRecord> findAppointmentById(@PathVariable Long id) {
        return appointmentService.findAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentRecord> findAppointmentsByDoctorId(@PathVariable Long doctorId) {
        return appointmentService.findAppointmentsByDoctorId(doctorId);
    }
}