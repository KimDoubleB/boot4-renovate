package com.kirndoubleb.labs.service

import com.kirndoubleb.labs.domain.model.Pet
import com.kirndoubleb.labs.domain.repository.PetRepository
import com.kirndoubleb.labs.exception.ResourceNotFoundException
import com.kirndoubleb.labs.metrics.PetClinicMetrics
import io.micrometer.observation.annotation.Observed
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@Observed(name = "pet.service")
class PetService(
    private val petRepository: PetRepository,
    private val ownerService: OwnerService,
    private val petClinicMetrics: PetClinicMetrics
) {

    fun findAll(pageable: Pageable): Page<Pet> {
        return petRepository.findAll(pageable)
    }

    fun findById(id: String): Pet {
        return petRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Pet", id) }
    }

    fun findByOwnerId(ownerId: String): List<Pet> {
        ownerService.findById(ownerId) // Validates owner exists
        return petRepository.findByOwnerId(ownerId)
    }

    fun create(pet: Pet): Pet {
        ownerService.findById(pet.ownerId) // Validates owner exists
        val saved = petRepository.save(pet)
        petClinicMetrics.incrementPetsCreated()
        return saved
    }

    fun update(id: String, updateFn: (Pet) -> Pet): Pet {
        val existing = findById(id)
        val updated = updateFn(existing).copy(
            id = existing.id,
            createdAt = existing.createdAt,
            updatedAt = Instant.now()
        )
        if (updated.ownerId != existing.ownerId) {
            ownerService.findById(updated.ownerId) // Validates new owner exists
        }
        return petRepository.save(updated)
    }

    fun delete(id: String) {
        if (!petRepository.existsById(id)) {
            throw ResourceNotFoundException("Pet", id)
        }
        petRepository.deleteById(id)
    }
}
