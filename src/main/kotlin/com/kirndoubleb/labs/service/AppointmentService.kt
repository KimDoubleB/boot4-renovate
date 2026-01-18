package com.kirndoubleb.labs.service

import com.kirndoubleb.labs.domain.model.Appointment
import com.kirndoubleb.labs.domain.model.AppointmentStatus
import com.kirndoubleb.labs.domain.model.Visit
import com.kirndoubleb.labs.domain.repository.AppointmentRepository
import com.kirndoubleb.labs.exception.InvalidOperationException
import com.kirndoubleb.labs.exception.ResourceNotFoundException
import com.kirndoubleb.labs.metrics.PetClinicMetrics
import com.kirndoubleb.labs.service.external.NotificationService
import io.micrometer.observation.annotation.Observed
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@Observed(name = "appointment.service")
class AppointmentService(
    private val appointmentRepository: AppointmentRepository,
    private val petService: PetService,
    private val vetService: VetService,
    private val visitService: VisitService,
    private val notificationService: NotificationService,
    private val petClinicMetrics: PetClinicMetrics
) {

    fun findAll(pageable: Pageable): Page<Appointment> {
        return appointmentRepository.findAll(pageable)
    }

    fun findById(id: String): Appointment {
        return appointmentRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Appointment", id) }
    }

    fun findByPetId(petId: String): List<Appointment> {
        petService.findById(petId) // Validates pet exists
        return appointmentRepository.findByPetId(petId)
    }

    fun create(appointment: Appointment): Appointment {
        petService.findById(appointment.petId) // Validates pet exists
        vetService.findById(appointment.vetId) // Validates vet exists

        val saved = appointmentRepository.save(appointment)
        petClinicMetrics.incrementAppointmentsScheduled()
        return saved
    }

    fun update(id: String, updateFn: (Appointment) -> Appointment): Appointment {
        val existing = findById(id)
        val updated = updateFn(existing).copy(
            id = existing.id,
            createdAt = existing.createdAt,
            updatedAt = Instant.now()
        )
        return appointmentRepository.save(updated)
    }

    fun confirm(id: String): Appointment {
        val appointment = findById(id)
        validateStatusTransition(appointment, AppointmentStatus.CONFIRMED)

        return appointmentRepository.save(
            appointment.copy(
                status = AppointmentStatus.CONFIRMED,
                updatedAt = Instant.now()
            )
        )
    }

    fun cancel(id: String): Appointment {
        val appointment = findById(id)
        validateStatusTransition(appointment, AppointmentStatus.CANCELLED)

        return appointmentRepository.save(
            appointment.copy(
                status = AppointmentStatus.CANCELLED,
                updatedAt = Instant.now()
            )
        )
    }

    fun complete(id: String, diagnosis: String?, treatment: String?): Visit {
        val appointment = findById(id)
        validateStatusTransition(appointment, AppointmentStatus.COMPLETED)

        appointmentRepository.save(
            appointment.copy(
                status = AppointmentStatus.COMPLETED,
                updatedAt = Instant.now()
            )
        )

        return visitService.create(
            Visit(
                petId = appointment.petId,
                vetId = appointment.vetId,
                diagnosis = diagnosis,
                treatment = treatment
            )
        )
    }

    fun sendReminder(id: String): Appointment {
        val appointment = findById(id)

        if (appointment.status != AppointmentStatus.SCHEDULED &&
            appointment.status != AppointmentStatus.CONFIRMED) {
            throw InvalidOperationException(
                "Cannot send reminder for appointment with status: ${appointment.status}"
            )
        }

        notificationService.sendAppointmentReminder(appointment)

        return appointmentRepository.save(
            appointment.copy(
                reminderSent = true,
                updatedAt = Instant.now()
            )
        )
    }

    private fun validateStatusTransition(appointment: Appointment, newStatus: AppointmentStatus) {
        val validTransitions = mapOf(
            AppointmentStatus.SCHEDULED to setOf(
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.CANCELLED
            ),
            AppointmentStatus.CONFIRMED to setOf(
                AppointmentStatus.COMPLETED,
                AppointmentStatus.CANCELLED,
                AppointmentStatus.NO_SHOW
            )
        )

        val allowedTransitions = validTransitions[appointment.status] ?: emptySet()
        if (newStatus !in allowedTransitions) {
            throw InvalidOperationException(
                "Cannot transition appointment from ${appointment.status} to $newStatus"
            )
        }
    }
}
