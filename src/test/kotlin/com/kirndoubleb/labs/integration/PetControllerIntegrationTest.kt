package com.kirndoubleb.labs.integration

import com.kirndoubleb.labs.api.dto.request.CreatePetRequest
import com.kirndoubleb.labs.domain.model.Species
import com.kirndoubleb.labs.domain.repository.OwnerRepository
import com.kirndoubleb.labs.domain.repository.PetRepository
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
import java.time.LocalDate

class PetControllerIntegrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var petRepository: PetRepository

    @Autowired
    private lateinit var ownerRepository: OwnerRepository

    @BeforeEach
    fun setup() {
        petRepository.deleteAll()
        ownerRepository.deleteAll()
    }

    @Test
    fun `should create pet for existing owner`() {
        val owner = ownerRepository.save(
            TestDataFactory.createOwner(email = "petowner@example.com")
        )

        val request = CreatePetRequest(
            name = "Buddy",
            species = Species.DOG,
            breed = "Golden Retriever",
            birthDate = LocalDate.now().minusYears(2),
            ownerId = owner.id!!
        )

        mockMvc.perform(
            post("/api/v1/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Buddy"))
            .andExpect(jsonPath("$.species").value("DOG"))
            .andExpect(jsonPath("$.ownerId").value(owner.id))
    }

    @Test
    fun `should get pets by owner id`() {
        val owner = ownerRepository.save(
            TestDataFactory.createOwner(email = "petowner2@example.com")
        )
        petRepository.save(TestDataFactory.createPet(name = "Buddy", ownerId = owner.id!!))
        petRepository.save(TestDataFactory.createPet(name = "Max", ownerId = owner.id!!))

        mockMvc.perform(get("/api/v1/owners/${owner.id}/pets"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    fun `should return 404 when creating pet with non-existent owner`() {
        val request = CreatePetRequest(
            name = "Buddy",
            species = Species.DOG,
            breed = "Golden Retriever",
            birthDate = LocalDate.now().minusYears(2),
            ownerId = "non-existent-owner"
        )

        mockMvc.perform(
            post("/api/v1/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
    }
}
