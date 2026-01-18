package com.kirndoubleb.labs.api.dto.response

import com.kirndoubleb.labs.domain.model.Vet
import java.time.Instant

data class VetResponse(
    val id: String,
    val firstName: String,
    val lastName: String,
    val licenseNumber: String,
    val specialties: List<String>,
    val available: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(vet: Vet) = VetResponse(
            id = vet.id ?: "",
            firstName = vet.firstName,
            lastName = vet.lastName,
            licenseNumber = vet.licenseNumber,
            specialties = vet.specialties,
            available = vet.available,
            createdAt = vet.createdAt,
            updatedAt = vet.updatedAt
        )
    }
}
