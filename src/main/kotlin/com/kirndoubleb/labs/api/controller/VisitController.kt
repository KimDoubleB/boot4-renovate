package com.kirndoubleb.labs.api.controller

import com.kirndoubleb.labs.api.dto.request.CreateVisitRequest
import com.kirndoubleb.labs.api.dto.request.ProcessPaymentRequest
import com.kirndoubleb.labs.api.dto.request.UpdateVisitRequest
import com.kirndoubleb.labs.api.dto.response.PagedResponse
import com.kirndoubleb.labs.api.dto.response.VisitResponse
import com.kirndoubleb.labs.domain.model.Visit
import com.kirndoubleb.labs.service.VisitService
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/visits")
class VisitController(
    private val visitService: VisitService
) {

    @GetMapping
    fun findAll(
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<PagedResponse<VisitResponse>> {
        val page = visitService.findAll(pageable)
        return ResponseEntity.ok(PagedResponse.from(page, VisitResponse::from))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<VisitResponse> {
        val visit = visitService.findById(id)
        return ResponseEntity.ok(VisitResponse.from(visit))
    }

    @PostMapping
    @RateLimiter(name = "createEndpoints")
    fun create(@RequestBody request: CreateVisitRequest): ResponseEntity<VisitResponse> {
        val visit = Visit(
            petId = request.petId,
            vetId = request.vetId,
            diagnosis = request.diagnosis,
            treatment = request.treatment,
            notes = request.notes,
            cost = request.cost
        )
        val created = visitService.create(visit)
        return ResponseEntity.status(HttpStatus.CREATED).body(VisitResponse.from(created))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody request: UpdateVisitRequest
    ): ResponseEntity<VisitResponse> {
        val updated = visitService.update(id) { existing ->
            existing.copy(
                diagnosis = request.diagnosis ?: existing.diagnosis,
                treatment = request.treatment ?: existing.treatment,
                notes = request.notes ?: existing.notes,
                cost = request.cost ?: existing.cost
            )
        }
        return ResponseEntity.ok(VisitResponse.from(updated))
    }

    @PostMapping("/{id}/payment")
    @RateLimiter(name = "createEndpoints")
    fun processPayment(
        @PathVariable id: String,
        @RequestBody request: ProcessPaymentRequest
    ): ResponseEntity<VisitResponse> {
        val visit = visitService.processPayment(id, request.amount)
        return ResponseEntity.ok(VisitResponse.from(visit))
    }
}
