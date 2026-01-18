package com.kirndoubleb.labs.integration

import com.kirndoubleb.labs.api.dto.request.CreateOwnerRequest
import com.kirndoubleb.labs.api.dto.request.UpdateOwnerRequest
import com.kirndoubleb.labs.domain.repository.OwnerRepository
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

class OwnerControllerIntegrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var ownerRepository: OwnerRepository

    @BeforeEach
    fun setup() {
        ownerRepository.deleteAll()
    }

    @Test
    fun `should create owner successfully`() {
        val request = CreateOwnerRequest(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phone = "010-1234-5678",
            address = "123 Main St"
        )

        mockMvc.perform(
            post("/api/v1/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"))
    }

    @Test
    fun `should get owner by id`() {
        val savedOwner = ownerRepository.save(
            TestDataFactory.createOwner(email = "existing@example.com")
        )

        mockMvc.perform(get("/api/v1/owners/${savedOwner.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(savedOwner.id))
            .andExpect(jsonPath("$.email").value("existing@example.com"))
    }

    @Test
    fun `should update owner`() {
        val savedOwner = ownerRepository.save(
            TestDataFactory.createOwner(email = "update@example.com")
        )

        val updateRequest = UpdateOwnerRequest(
            firstName = "Jane",
            lastName = "Smith"
        )

        mockMvc.perform(
            put("/api/v1/owners/${savedOwner.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.firstName").value("Jane"))
            .andExpect(jsonPath("$.lastName").value("Smith"))
    }

    @Test
    fun `should delete owner`() {
        val savedOwner = ownerRepository.save(
            TestDataFactory.createOwner(email = "delete@example.com")
        )

        mockMvc.perform(delete("/api/v1/owners/${savedOwner.id}"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should get all owners with pagination`() {
        ownerRepository.saveAll(
            listOf(
                TestDataFactory.createOwner(email = "owner1@example.com"),
                TestDataFactory.createOwner(email = "owner2@example.com"),
                TestDataFactory.createOwner(email = "owner3@example.com")
            )
        )

        mockMvc.perform(get("/api/v1/owners"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(3))
            .andExpect(jsonPath("$.totalElements").value(3))
    }

    @Test
    fun `should return 404 for non-existent owner`() {
        mockMvc.perform(get("/api/v1/owners/non-existent-id"))
            .andExpect(status().isNotFound)
    }
}
