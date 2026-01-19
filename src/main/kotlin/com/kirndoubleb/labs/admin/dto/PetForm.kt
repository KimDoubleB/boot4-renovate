package com.kirndoubleb.labs.admin.dto

import com.kirndoubleb.labs.domain.model.Pet
import com.kirndoubleb.labs.domain.model.Species
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class PetForm(
    @field:NotBlank(message = "이름을 입력해주세요")
    var name: String = "",

    @field:NotNull(message = "종류를 선택해주세요")
    var species: Species? = null,

    var breed: String? = null,

    var birthDate: LocalDate? = null,

    @field:NotBlank(message = "보호자를 선택해주세요")
    var ownerId: String = ""
) {
    fun toEntity(): Pet = Pet(
        name = name,
        species = species!!,
        breed = breed,
        birthDate = birthDate,
        ownerId = ownerId
    )

    companion object {
        fun from(pet: Pet): PetForm = PetForm(
            name = pet.name,
            species = pet.species,
            breed = pet.breed,
            birthDate = pet.birthDate,
            ownerId = pet.ownerId
        )
    }
}
