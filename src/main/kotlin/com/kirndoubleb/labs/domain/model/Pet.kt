package com.kirndoubleb.labs.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.LocalDate

enum class Species {
    DOG, CAT, BIRD, REPTILE, FISH, HAMSTER, RABBIT, OTHER
}

@Document(collection = "pets")
data class Pet(
    @Id
    val id: String? = null,
    val name: String,
    val species: Species,
    val breed: String? = null,
    val birthDate: LocalDate? = null,
    val ownerId: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
