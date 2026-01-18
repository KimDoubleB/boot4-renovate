package com.kirndoubleb.labs.service

import com.kirndoubleb.labs.domain.model.PaymentStatus
import com.kirndoubleb.labs.domain.model.Visit
import com.kirndoubleb.labs.domain.repository.VisitRepository
import com.kirndoubleb.labs.exception.InvalidOperationException
import com.kirndoubleb.labs.exception.ResourceNotFoundException
import com.kirndoubleb.labs.service.external.PaymentService
import io.micrometer.observation.annotation.Observed
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant

@Service
@Observed(name = "visit.service")
class VisitService(
    private val visitRepository: VisitRepository,
    private val petService: PetService,
    private val vetService: VetService,
    private val paymentService: PaymentService
) {

    fun findAll(pageable: Pageable): Page<Visit> {
        return visitRepository.findAll(pageable)
    }

    fun findById(id: String): Visit {
        return visitRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Visit", id) }
    }

    fun findByPetId(petId: String): List<Visit> {
        petService.findById(petId) // Validates pet exists
        return visitRepository.findByPetId(petId)
    }

    fun create(visit: Visit): Visit {
        petService.findById(visit.petId) // Validates pet exists
        vetService.findById(visit.vetId) // Validates vet exists
        return visitRepository.save(visit)
    }

    fun update(id: String, updateFn: (Visit) -> Visit): Visit {
        val existing = findById(id)
        val updated = updateFn(existing).copy(
            id = existing.id,
            createdAt = existing.createdAt,
            updatedAt = Instant.now()
        )
        return visitRepository.save(updated)
    }

    fun processPayment(id: String, amount: BigDecimal): Visit {
        val visit = findById(id)

        if (visit.paymentStatus == PaymentStatus.COMPLETED) {
            throw InvalidOperationException("Payment already completed for visit: $id")
        }

        val paymentSuccess = paymentService.processPayment(id, amount)

        val newStatus = if (paymentSuccess) PaymentStatus.COMPLETED else PaymentStatus.FAILED
        return visitRepository.save(
            visit.copy(
                paymentStatus = newStatus,
                updatedAt = Instant.now()
            )
        )
    }
}
