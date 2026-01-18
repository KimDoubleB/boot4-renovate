package com.kirndoubleb.labs.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

enum class AppointmentStatus {
    SCHEDULED, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW
}

@Document(collection = "appointments")
data class Appointment(
    @Id
    val id: String? = null,
    val petId: String,
    val vetId: String,
    val scheduledAt: Instant,
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val reason: String? = null,
    val reminderSent: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
