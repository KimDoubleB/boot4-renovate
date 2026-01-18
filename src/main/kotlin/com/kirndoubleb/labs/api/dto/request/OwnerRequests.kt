package com.kirndoubleb.labs.api.dto.request

data class CreateOwnerRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: String? = null
)

data class UpdateOwnerRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null
)
