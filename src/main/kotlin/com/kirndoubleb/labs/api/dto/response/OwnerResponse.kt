package com.kirndoubleb.labs.api.dto.response

import com.kirndoubleb.labs.domain.model.Owner
import java.time.Instant

data class OwnerResponse(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: String?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(owner: Owner) = OwnerResponse(
            id = owner.id ?: "",
            firstName = owner.firstName,
            lastName = owner.lastName,
            email = owner.email,
            phone = owner.phone,
            address = owner.address,
            createdAt = owner.createdAt,
            updatedAt = owner.updatedAt
        )
    }
}
