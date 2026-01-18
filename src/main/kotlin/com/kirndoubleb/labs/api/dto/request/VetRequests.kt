package com.kirndoubleb.labs.api.dto.request

data class CreateVetRequest(
    val firstName: String,
    val lastName: String,
    val licenseNumber: String,
    val specialties: List<String> = emptyList()
)

data class UpdateVetRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val specialties: List<String>? = null,
    val available: Boolean? = null
)
