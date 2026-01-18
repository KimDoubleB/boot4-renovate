package com.kirndoubleb.labs.support

import com.kirndoubleb.labs.domain.model.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object TestDataFactory {

    fun createOwner(
        id: String? = null,
        firstName: String = "John",
        lastName: String = "Doe",
        email: String = "john.doe@example.com",
        phone: String = "010-1234-5678",
        address: String? = "123 Main St"
    ) = Owner(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        address = address
    )

    fun createPet(
        id: String? = null,
        name: String = "Buddy",
        species: Species = Species.DOG,
        breed: String? = "Golden Retriever",
        birthDate: LocalDate? = LocalDate.now().minusYears(2),
        ownerId: String = "owner-123"
    ) = Pet(
        id = id,
        name = name,
        species = species,
        breed = breed,
        birthDate = birthDate,
        ownerId = ownerId
    )

    fun createVet(
        id: String? = null,
        firstName: String = "Dr. Jane",
        lastName: String = "Smith",
        licenseNumber: String = "VET-12345",
        specialties: List<String> = listOf("Surgery", "Dermatology"),
        available: Boolean = true
    ) = Vet(
        id = id,
        firstName = firstName,
        lastName = lastName,
        licenseNumber = licenseNumber,
        specialties = specialties,
        available = available
    )

    fun createVisit(
        id: String? = null,
        petId: String = "pet-123",
        vetId: String = "vet-123",
        diagnosis: String? = "Routine checkup",
        treatment: String? = "Vaccination",
        notes: String? = "Pet in good health",
        cost: BigDecimal = BigDecimal("50.00"),
        paymentStatus: PaymentStatus = PaymentStatus.PENDING
    ) = Visit(
        id = id,
        petId = petId,
        vetId = vetId,
        diagnosis = diagnosis,
        treatment = treatment,
        notes = notes,
        cost = cost,
        paymentStatus = paymentStatus
    )

    fun createAppointment(
        id: String? = null,
        petId: String = "pet-123",
        vetId: String = "vet-123",
        scheduledAt: Instant = Instant.now().plus(1, ChronoUnit.DAYS),
        status: AppointmentStatus = AppointmentStatus.SCHEDULED,
        reason: String? = "Annual checkup",
        reminderSent: Boolean = false
    ) = Appointment(
        id = id,
        petId = petId,
        vetId = vetId,
        scheduledAt = scheduledAt,
        status = status,
        reason = reason,
        reminderSent = reminderSent
    )
}
