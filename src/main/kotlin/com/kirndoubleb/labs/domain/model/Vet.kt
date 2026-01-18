package com.kirndoubleb.labs.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "vets")
data class Vet(
    @Id
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val licenseNumber: String,
    val specialties: List<String> = emptyList(),
    val available: Boolean = true,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
