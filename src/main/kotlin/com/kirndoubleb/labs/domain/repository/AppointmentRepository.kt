package com.kirndoubleb.labs.domain.repository

import com.kirndoubleb.labs.domain.model.Appointment
import com.kirndoubleb.labs.domain.model.AppointmentStatus
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

interface AppointmentRepository : MongoRepository<Appointment, String> {
    fun findByPetId(petId: String): List<Appointment>
    fun findByVetId(vetId: String): List<Appointment>
    fun findByStatus(status: AppointmentStatus): List<Appointment>
    fun findByScheduledAtBetween(start: Instant, end: Instant): List<Appointment>
    fun findByReminderSentFalseAndScheduledAtBefore(time: Instant): List<Appointment>
}
