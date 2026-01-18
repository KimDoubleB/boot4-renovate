package com.kirndoubleb.labs.api.dto.response

import com.kirndoubleb.labs.domain.model.Appointment
import com.kirndoubleb.labs.domain.model.AppointmentStatus
import java.time.Instant

data class AppointmentResponse(
    val id: String,
    val petId: String,
    val vetId: String,
    val scheduledAt: Instant,
    val status: AppointmentStatus,
    val reason: String?,
    val reminderSent: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(appointment: Appointment) = AppointmentResponse(
            id = appointment.id ?: "",
            petId = appointment.petId,
            vetId = appointment.vetId,
            scheduledAt = appointment.scheduledAt,
            status = appointment.status,
            reason = appointment.reason,
            reminderSent = appointment.reminderSent,
            createdAt = appointment.createdAt,
            updatedAt = appointment.updatedAt
        )
    }
}
