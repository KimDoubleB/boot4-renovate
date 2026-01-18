package com.kirndoubleb.labs.service

import com.kirndoubleb.labs.domain.model.Vet
import com.kirndoubleb.labs.domain.repository.VetRepository
import com.kirndoubleb.labs.exception.DuplicateResourceException
import com.kirndoubleb.labs.exception.ResourceNotFoundException
import io.micrometer.observation.annotation.Observed
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@Observed(name = "vet.service")
class VetService(
    private val vetRepository: VetRepository
) {

    fun findAll(pageable: Pageable): Page<Vet> {
        return vetRepository.findAll(pageable)
    }

    fun findById(id: String): Vet {
        return vetRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Vet", id) }
    }

    fun findAvailable(): List<Vet> {
        return vetRepository.findByAvailableTrue()
    }

    fun findBySpecialty(specialty: String): List<Vet> {
        return vetRepository.findBySpecialtiesContaining(specialty)
    }

    fun create(vet: Vet): Vet {
        vetRepository.findByLicenseNumber(vet.licenseNumber)?.let {
            throw DuplicateResourceException("Vet", "licenseNumber", vet.licenseNumber)
        }
        return vetRepository.save(vet)
    }

    fun update(id: String, updateFn: (Vet) -> Vet): Vet {
        val existing = findById(id)
        val updated = updateFn(existing).copy(
            id = existing.id,
            createdAt = existing.createdAt,
            updatedAt = Instant.now()
        )
        return vetRepository.save(updated)
    }

    fun delete(id: String) {
        if (!vetRepository.existsById(id)) {
            throw ResourceNotFoundException("Vet", id)
        }
        vetRepository.deleteById(id)
    }
}
