package com.kirndoubleb.labs.domain.repository

import com.kirndoubleb.labs.domain.model.Vet
import org.springframework.data.mongodb.repository.MongoRepository

interface VetRepository : MongoRepository<Vet, String> {
    fun findByAvailableTrue(): List<Vet>
    fun findBySpecialtiesContaining(specialty: String): List<Vet>
    fun findByLicenseNumber(licenseNumber: String): Vet?
}
