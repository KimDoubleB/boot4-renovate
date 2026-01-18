package com.kirndoubleb.labs.api.controller

import com.kirndoubleb.labs.api.dto.request.CreateVetRequest
import com.kirndoubleb.labs.api.dto.request.UpdateVetRequest
import com.kirndoubleb.labs.api.dto.response.PagedResponse
import com.kirndoubleb.labs.api.dto.response.VetResponse
import com.kirndoubleb.labs.domain.model.Vet
import com.kirndoubleb.labs.service.VetService
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/vets")
class VetController(
    private val vetService: VetService
) {

    @GetMapping
    fun findAll(
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<PagedResponse<VetResponse>> {
        val page = vetService.findAll(pageable)
        return ResponseEntity.ok(PagedResponse.from(page, VetResponse::from))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<VetResponse> {
        val vet = vetService.findById(id)
        return ResponseEntity.ok(VetResponse.from(vet))
    }

    @GetMapping("/available")
    fun findAvailable(): ResponseEntity<List<VetResponse>> {
        val vets = vetService.findAvailable()
        return ResponseEntity.ok(vets.map(VetResponse::from))
    }

    @PostMapping
    @RateLimiter(name = "createEndpoints")
    fun create(@RequestBody request: CreateVetRequest): ResponseEntity<VetResponse> {
        val vet = Vet(
            firstName = request.firstName,
            lastName = request.lastName,
            licenseNumber = request.licenseNumber,
            specialties = request.specialties
        )
        val created = vetService.create(vet)
        return ResponseEntity.status(HttpStatus.CREATED).body(VetResponse.from(created))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody request: UpdateVetRequest
    ): ResponseEntity<VetResponse> {
        val updated = vetService.update(id) { existing ->
            existing.copy(
                firstName = request.firstName ?: existing.firstName,
                lastName = request.lastName ?: existing.lastName,
                specialties = request.specialties ?: existing.specialties,
                available = request.available ?: existing.available
            )
        }
        return ResponseEntity.ok(VetResponse.from(updated))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        vetService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
