package com.kirndoubleb.labs.api.dto.response

import com.kirndoubleb.labs.domain.model.Pet
import com.kirndoubleb.labs.domain.model.Species
import java.time.Instant
import java.time.LocalDate

data class PetResponse(
    val id: String,
    val name: String,
    val species: Species,
    val breed: String?,
    val birthDate: LocalDate?,
    val ownerId: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(pet: Pet) = PetResponse(
            id = pet.id ?: "",
            name = pet.name,
            species = pet.species,
            breed = pet.breed,
            birthDate = pet.birthDate,
            ownerId = pet.ownerId,
            createdAt = pet.createdAt,
            updatedAt = pet.updatedAt
        )
    }
}
