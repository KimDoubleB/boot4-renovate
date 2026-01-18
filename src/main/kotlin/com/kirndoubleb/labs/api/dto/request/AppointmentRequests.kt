package com.kirndoubleb.labs.api.dto.request

import java.time.Instant

data class CreateAppointmentRequest(
    val petId: String,
    val vetId: String,
    val scheduledAt: Instant,
    val reason: String? = null
)

data class UpdateAppointmentRequest(
    val scheduledAt: Instant? = null,
    val reason: String? = null
)

data class CompleteAppointmentRequest(
    val diagnosis: String? = null,
    val treatment: String? = null
)
