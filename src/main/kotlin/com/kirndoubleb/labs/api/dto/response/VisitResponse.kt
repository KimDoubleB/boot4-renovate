package com.kirndoubleb.labs.api.dto.response

import com.kirndoubleb.labs.domain.model.PaymentStatus
import com.kirndoubleb.labs.domain.model.Visit
import java.math.BigDecimal
import java.time.Instant

data class VisitResponse(
    val id: String,
    val petId: String,
    val vetId: String,
    val visitDate: Instant,
    val diagnosis: String?,
    val treatment: String?,
    val notes: String?,
    val cost: BigDecimal,
    val paymentStatus: PaymentStatus,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(visit: Visit) = VisitResponse(
            id = visit.id ?: "",
            petId = visit.petId,
            vetId = visit.vetId,
            visitDate = visit.visitDate,
            diagnosis = visit.diagnosis,
            treatment = visit.treatment,
            notes = visit.notes,
            cost = visit.cost,
            paymentStatus = visit.paymentStatus,
            createdAt = visit.createdAt,
            updatedAt = visit.updatedAt
        )
    }
}
