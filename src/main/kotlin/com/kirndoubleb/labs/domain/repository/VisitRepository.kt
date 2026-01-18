package com.kirndoubleb.labs.domain.repository

import com.kirndoubleb.labs.domain.model.PaymentStatus
import com.kirndoubleb.labs.domain.model.Visit
import org.springframework.data.mongodb.repository.MongoRepository

interface VisitRepository : MongoRepository<Visit, String> {
    fun findByPetId(petId: String): List<Visit>
    fun findByVetId(vetId: String): List<Visit>
    fun findByPaymentStatus(paymentStatus: PaymentStatus): List<Visit>
}
