package com.kirndoubleb.labs.api.dto.request

import java.math.BigDecimal

data class CreateVisitRequest(
    val petId: String,
    val vetId: String,
    val diagnosis: String? = null,
    val treatment: String? = null,
    val notes: String? = null,
    val cost: BigDecimal = BigDecimal.ZERO
)

data class UpdateVisitRequest(
    val diagnosis: String? = null,
    val treatment: String? = null,
    val notes: String? = null,
    val cost: BigDecimal? = null
)

data class ProcessPaymentRequest(
    val amount: BigDecimal
)
