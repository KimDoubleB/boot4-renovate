package com.kirndoubleb.labs.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

enum class PaymentStatus {
    PENDING, COMPLETED, FAILED, REFUNDED
}

@Document(collection = "visits")
data class Visit(
    @Id
    val id: String? = null,
    val petId: String,
    val vetId: String,
    val visitDate: Instant = Instant.now(),
    val diagnosis: String? = null,
    val treatment: String? = null,
    val notes: String? = null,
    val cost: BigDecimal = BigDecimal.ZERO,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
