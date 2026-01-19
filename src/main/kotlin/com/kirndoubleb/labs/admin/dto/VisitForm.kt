package com.kirndoubleb.labs.admin.dto

import com.kirndoubleb.labs.domain.model.PaymentStatus
import com.kirndoubleb.labs.domain.model.Visit
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class VisitForm(
    @field:NotBlank(message = "반려동물을 선택해주세요")
    var petId: String = "",

    @field:NotBlank(message = "수의사를 선택해주세요")
    var vetId: String = "",

    var visitDateTime: LocalDateTime? = null,

    var diagnosis: String? = null,

    var treatment: String? = null,

    var notes: String? = null,

    @field:NotNull(message = "비용을 입력해주세요")
    @field:PositiveOrZero(message = "비용은 0 이상이어야 합니다")
    var cost: BigDecimal = BigDecimal.ZERO,

    var paymentStatus: PaymentStatus = PaymentStatus.PENDING
) {
    fun toEntity(): Visit = Visit(
        petId = petId,
        vetId = vetId,
        visitDate = visitDateTime?.atZone(ZoneId.systemDefault())?.toInstant() ?: Instant.now(),
        diagnosis = diagnosis,
        treatment = treatment,
        notes = notes,
        cost = cost,
        paymentStatus = paymentStatus
    )

    companion object {
        fun from(visit: Visit): VisitForm = VisitForm(
            petId = visit.petId,
            vetId = visit.vetId,
            visitDateTime = LocalDateTime.ofInstant(visit.visitDate, ZoneId.systemDefault()),
            diagnosis = visit.diagnosis,
            treatment = visit.treatment,
            notes = visit.notes,
            cost = visit.cost,
            paymentStatus = visit.paymentStatus
        )
    }
}
