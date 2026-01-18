package com.kirndoubleb.labs.domain.repository

import com.kirndoubleb.labs.domain.model.Pet
import com.kirndoubleb.labs.domain.model.Species
import org.springframework.data.mongodb.repository.MongoRepository

interface PetRepository : MongoRepository<Pet, String> {
    fun findByOwnerId(ownerId: String): List<Pet>
    fun findBySpecies(species: Species): List<Pet>
    fun findByNameContainingIgnoreCase(name: String): List<Pet>
}
