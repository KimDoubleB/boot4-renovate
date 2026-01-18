package com.kirndoubleb.labs.api.dto.request

import com.kirndoubleb.labs.domain.model.Species
import java.time.LocalDate

data class CreatePetRequest(
    val name: String,
    val species: Species,
    val breed: String? = null,
    val birthDate: LocalDate? = null,
    val ownerId: String
)

data class UpdatePetRequest(
    val name: String? = null,
    val species: Species? = null,
    val breed: String? = null,
    val birthDate: LocalDate? = null,
    val ownerId: String? = null
)
