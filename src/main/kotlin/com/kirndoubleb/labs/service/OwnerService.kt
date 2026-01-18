package com.kirndoubleb.labs.service

import com.kirndoubleb.labs.domain.model.Owner
import com.kirndoubleb.labs.domain.repository.OwnerRepository
import com.kirndoubleb.labs.exception.DuplicateResourceException
import com.kirndoubleb.labs.exception.ResourceNotFoundException
import io.micrometer.observation.annotation.Observed
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@Observed(name = "owner.service")
class OwnerService(
    private val ownerRepository: OwnerRepository
) {

    fun findAll(pageable: Pageable): Page<Owner> {
        return ownerRepository.findAll(pageable)
    }

    fun findById(id: String): Owner {
        return ownerRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Owner", id) }
    }

    fun create(owner: Owner): Owner {
        ownerRepository.findByEmail(owner.email)?.let {
            throw DuplicateResourceException("Owner", "email", owner.email)
        }
        return ownerRepository.save(owner)
    }

    fun update(id: String, updateFn: (Owner) -> Owner): Owner {
        val existing = findById(id)
        val updated = updateFn(existing).copy(
            id = existing.id,
            createdAt = existing.createdAt,
            updatedAt = Instant.now()
        )
        return ownerRepository.save(updated)
    }

    fun delete(id: String) {
        if (!ownerRepository.existsById(id)) {
            throw ResourceNotFoundException("Owner", id)
        }
        ownerRepository.deleteById(id)
    }

    fun searchByLastName(lastName: String): List<Owner> {
        return ownerRepository.findByLastNameContainingIgnoreCase(lastName)
    }
}
