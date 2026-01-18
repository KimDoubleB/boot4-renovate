package com.kirndoubleb.labs.api.controller

import com.kirndoubleb.labs.api.dto.request.CreateOwnerRequest
import com.kirndoubleb.labs.api.dto.request.UpdateOwnerRequest
import com.kirndoubleb.labs.api.dto.response.OwnerResponse
import com.kirndoubleb.labs.api.dto.response.PagedResponse
import com.kirndoubleb.labs.api.dto.response.PetResponse
import com.kirndoubleb.labs.domain.model.Owner
import com.kirndoubleb.labs.service.OwnerService
import com.kirndoubleb.labs.service.PetService
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/owners")
class OwnerController(
    private val ownerService: OwnerService,
    private val petService: PetService
) {

    @GetMapping
    fun findAll(
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<PagedResponse<OwnerResponse>> {
        val page = ownerService.findAll(pageable)
        return ResponseEntity.ok(PagedResponse.from(page, OwnerResponse::from))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<OwnerResponse> {
        val owner = ownerService.findById(id)
        return ResponseEntity.ok(OwnerResponse.from(owner))
    }

    @PostMapping
    @RateLimiter(name = "createEndpoints")
    fun create(@RequestBody request: CreateOwnerRequest): ResponseEntity<OwnerResponse> {
        val owner = Owner(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            phone = request.phone,
            address = request.address
        )
        val created = ownerService.create(owner)
        return ResponseEntity.status(HttpStatus.CREATED).body(OwnerResponse.from(created))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody request: UpdateOwnerRequest
    ): ResponseEntity<OwnerResponse> {
        val updated = ownerService.update(id) { existing ->
            existing.copy(
                firstName = request.firstName ?: existing.firstName,
                lastName = request.lastName ?: existing.lastName,
                email = request.email ?: existing.email,
                phone = request.phone ?: existing.phone,
                address = request.address ?: existing.address
            )
        }
        return ResponseEntity.ok(OwnerResponse.from(updated))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        ownerService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/pets")
    fun findPetsByOwnerId(@PathVariable id: String): ResponseEntity<List<PetResponse>> {
        val pets = petService.findByOwnerId(id)
        return ResponseEntity.ok(pets.map(PetResponse::from))
    }
}
