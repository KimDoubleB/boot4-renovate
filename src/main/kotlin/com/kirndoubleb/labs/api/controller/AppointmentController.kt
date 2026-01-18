package com.kirndoubleb.labs.api.controller

import com.kirndoubleb.labs.api.dto.request.CompleteAppointmentRequest
import com.kirndoubleb.labs.api.dto.request.CreateAppointmentRequest
import com.kirndoubleb.labs.api.dto.request.UpdateAppointmentRequest
import com.kirndoubleb.labs.api.dto.response.AppointmentResponse
import com.kirndoubleb.labs.api.dto.response.PagedResponse
import com.kirndoubleb.labs.api.dto.response.VisitResponse
import com.kirndoubleb.labs.domain.model.Appointment
import com.kirndoubleb.labs.service.AppointmentService
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/appointments")
class AppointmentController(
    private val appointmentService: AppointmentService
) {

    @GetMapping
    fun findAll(
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<PagedResponse<AppointmentResponse>> {
        val page = appointmentService.findAll(pageable)
        return ResponseEntity.ok(PagedResponse.from(page, AppointmentResponse::from))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<AppointmentResponse> {
        val appointment = appointmentService.findById(id)
        return ResponseEntity.ok(AppointmentResponse.from(appointment))
    }

    @PostMapping
    @RateLimiter(name = "createEndpoints")
    fun create(@RequestBody request: CreateAppointmentRequest): ResponseEntity<AppointmentResponse> {
        val appointment = Appointment(
            petId = request.petId,
            vetId = request.vetId,
            scheduledAt = request.scheduledAt,
            reason = request.reason
        )
        val created = appointmentService.create(appointment)
        return ResponseEntity.status(HttpStatus.CREATED).body(AppointmentResponse.from(created))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody request: UpdateAppointmentRequest
    ): ResponseEntity<AppointmentResponse> {
        val updated = appointmentService.update(id) { existing ->
            existing.copy(
                scheduledAt = request.scheduledAt ?: existing.scheduledAt,
                reason = request.reason ?: existing.reason
            )
        }
        return ResponseEntity.ok(AppointmentResponse.from(updated))
    }

    @PostMapping("/{id}/confirm")
    fun confirm(@PathVariable id: String): ResponseEntity<AppointmentResponse> {
        val appointment = appointmentService.confirm(id)
        return ResponseEntity.ok(AppointmentResponse.from(appointment))
    }

    @PostMapping("/{id}/cancel")
    fun cancel(@PathVariable id: String): ResponseEntity<AppointmentResponse> {
        val appointment = appointmentService.cancel(id)
        return ResponseEntity.ok(AppointmentResponse.from(appointment))
    }

    @PostMapping("/{id}/complete")
    fun complete(
        @PathVariable id: String,
        @RequestBody request: CompleteAppointmentRequest
    ): ResponseEntity<VisitResponse> {
        val visit = appointmentService.complete(id, request.diagnosis, request.treatment)
        return ResponseEntity.status(HttpStatus.CREATED).body(VisitResponse.from(visit))
    }

    @PostMapping("/{id}/notify")
    fun sendReminder(@PathVariable id: String): ResponseEntity<AppointmentResponse> {
        val appointment = appointmentService.sendReminder(id)
        return ResponseEntity.ok(AppointmentResponse.from(appointment))
    }
}
