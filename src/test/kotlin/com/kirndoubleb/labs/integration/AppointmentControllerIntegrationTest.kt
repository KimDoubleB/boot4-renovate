package com.kirndoubleb.labs.integration

import com.kirndoubleb.labs.api.dto.request.CompleteAppointmentRequest
import com.kirndoubleb.labs.api.dto.request.CreateAppointmentRequest
import com.kirndoubleb.labs.domain.model.AppointmentStatus
import com.kirndoubleb.labs.domain.repository.*
import com.kirndoubleb.labs.support.IntegrationTestBase
import com.kirndoubleb.labs.support.TestDataFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import tools.jackson.databind.ObjectMapper
import java.time.Instant
import java.time.temporal.ChronoUnit

class AppointmentControllerIntegrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var appointmentRepository: AppointmentRepository

    @Autowired
    private lateinit var petRepository: PetRepository

    @Autowired
    private lateinit var vetRepository: VetRepository

    @Autowired
    private lateinit var ownerRepository: OwnerRepository

    @Autowired
    private lateinit var visitRepository: VisitRepository

    @BeforeEach
    fun setup() {
        appointmentRepository.deleteAll()
        visitRepository.deleteAll()
        petRepository.deleteAll()
        vetRepository.deleteAll()
        ownerRepository.deleteAll()
    }

    @Test
    fun `should create appointment`() {
        val owner = ownerRepository.save(TestDataFactory.createOwner())
        val pet = petRepository.save(TestDataFactory.createPet(ownerId = owner.id!!))
        val vet = vetRepository.save(TestDataFactory.createVet())

        val request = CreateAppointmentRequest(
            petId = pet.id!!,
            vetId = vet.id!!,
            scheduledAt = Instant.now().plus(1, ChronoUnit.DAYS),
            reason = "Annual checkup"
        )

        mockMvc.perform(
            post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.petId").value(pet.id))
            .andExpect(jsonPath("$.vetId").value(vet.id))
            .andExpect(jsonPath("$.status").value("SCHEDULED"))
    }

    @Test
    fun `should confirm appointment`() {
        val owner = ownerRepository.save(TestDataFactory.createOwner(email = "confirm@example.com"))
        val pet = petRepository.save(TestDataFactory.createPet(ownerId = owner.id!!))
        val vet = vetRepository.save(TestDataFactory.createVet(licenseNumber = "VET-CONFIRM"))
        val appointment = appointmentRepository.save(
            TestDataFactory.createAppointment(petId = pet.id!!, vetId = vet.id!!)
        )

        mockMvc.perform(post("/api/v1/appointments/${appointment.id}/confirm"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("CONFIRMED"))
    }

    @Test
    fun `should complete appointment and create visit`() {
        val owner = ownerRepository.save(TestDataFactory.createOwner(email = "complete@example.com"))
        val pet = petRepository.save(TestDataFactory.createPet(ownerId = owner.id!!))
        val vet = vetRepository.save(TestDataFactory.createVet(licenseNumber = "VET-COMPLETE"))
        val appointment = appointmentRepository.save(
            TestDataFactory.createAppointment(
                petId = pet.id!!,
                vetId = vet.id!!,
                status = AppointmentStatus.CONFIRMED
            )
        )

        val request = CompleteAppointmentRequest(
            diagnosis = "Healthy",
            treatment = "Vaccination"
        )

        mockMvc.perform(
            post("/api/v1/appointments/${appointment.id}/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.petId").value(pet.id))
            .andExpect(jsonPath("$.vetId").value(vet.id))
            .andExpect(jsonPath("$.diagnosis").value("Healthy"))
    }

    @Test
    fun `should cancel appointment`() {
        val owner = ownerRepository.save(TestDataFactory.createOwner(email = "cancel@example.com"))
        val pet = petRepository.save(TestDataFactory.createPet(ownerId = owner.id!!))
        val vet = vetRepository.save(TestDataFactory.createVet(licenseNumber = "VET-CANCEL"))
        val appointment = appointmentRepository.save(
            TestDataFactory.createAppointment(
                petId = pet.id!!,
                vetId = vet.id!!,
                status = AppointmentStatus.CONFIRMED
            )
        )

        mockMvc.perform(post("/api/v1/appointments/${appointment.id}/cancel"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("CANCELLED"))
    }
}
