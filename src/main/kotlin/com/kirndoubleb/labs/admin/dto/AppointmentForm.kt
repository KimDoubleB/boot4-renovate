package com.kirndoubleb.labs.admin.dto

import com.kirndoubleb.labs.domain.model.Appointment
import com.kirndoubleb.labs.domain.model.AppointmentStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class AppointmentForm(
    @field:NotBlank(message = "반려동물을 선택해주세요")
    var petId: String = "",

    @field:NotBlank(message = "수의사를 선택해주세요")
    var vetId: String = "",

    @field:NotNull(message = "예약 일시를 입력해주세요")
    var scheduledDateTime: LocalDateTime? = null,

    var status: AppointmentStatus = AppointmentStatus.SCHEDULED,

    var reason: String? = null
) {
    fun toEntity(): Appointment = Appointment(
        petId = petId,
        vetId = vetId,
        scheduledAt = scheduledDateTime?.atZone(ZoneId.systemDefault())?.toInstant()
            ?: throw IllegalStateException("scheduledDateTime is required"),
        status = status,
        reason = reason
    )

    companion object {
        fun from(appointment: Appointment): AppointmentForm = AppointmentForm(
            petId = appointment.petId,
            vetId = appointment.vetId,
            scheduledDateTime = LocalDateTime.ofInstant(appointment.scheduledAt, ZoneId.systemDefault()),
            status = appointment.status,
            reason = appointment.reason
        )
    }
}
