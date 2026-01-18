package com.kirndoubleb.labs.api.controller

import com.kirndoubleb.labs.api.dto.request.CreatePetRequest
import com.kirndoubleb.labs.api.dto.request.UpdatePetRequest
import com.kirndoubleb.labs.api.dto.response.AppointmentResponse
import com.kirndoubleb.labs.api.dto.response.PagedResponse
import com.kirndoubleb.labs.api.dto.response.PetResponse
import com.kirndoubleb.labs.api.dto.response.VisitResponse
import com.kirndoubleb.labs.domain.model.Pet
import com.kirndoubleb.labs.service.AppointmentService
import com.kirndoubleb.labs.service.PetService
import com.kirndoubleb.labs.service.VisitService
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/pets")
class PetController(
    private val petService: PetService,
    private val visitService: VisitService,
    private val appointmentService: AppointmentService
) {

    @GetMapping
    fun findAll(
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<PagedResponse<PetResponse>> {
        val page = petService.findAll(pageable)
        return ResponseEntity.ok(PagedResponse.from(page, PetResponse::from))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<PetResponse> {
        val pet = petService.findById(id)
        return ResponseEntity.ok(PetResponse.from(pet))
    }

    @PostMapping
    @RateLimiter(name = "createEndpoints")
    fun create(@RequestBody request: CreatePetRequest): ResponseEntity<PetResponse> {
        val pet = Pet(
            name = request.name,
            species = request.species,
            breed = request.breed,
            birthDate = request.birthDate,
            ownerId = request.ownerId
        )
        val created = petService.create(pet)
        return ResponseEntity.status(HttpStatus.CREATED).body(PetResponse.from(created))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody request: UpdatePetRequest
    ): ResponseEntity<PetResponse> {
        val updated = petService.update(id) { existing ->
            existing.copy(
                name = request.name ?: existing.name,
                species = request.species ?: existing.species,
                breed = request.breed ?: existing.breed,
                birthDate = request.birthDate ?: existing.birthDate,
                ownerId = request.ownerId ?: existing.ownerId
            )
        }
        return ResponseEntity.ok(PetResponse.from(updated))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        petService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/visits")
    fun findVisitsByPetId(@PathVariable id: String): ResponseEntity<List<VisitResponse>> {
        val visits = visitService.findByPetId(id)
        return ResponseEntity.ok(visits.map(VisitResponse::from))
    }

    @GetMapping("/{id}/appointments")
    fun findAppointmentsByPetId(@PathVariable id: String): ResponseEntity<List<AppointmentResponse>> {
        val appointments = appointmentService.findByPetId(id)
        return ResponseEntity.ok(appointments.map(AppointmentResponse::from))
    }
}
