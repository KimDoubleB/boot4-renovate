package com.kirndoubleb.labs.domain.repository

import com.kirndoubleb.labs.domain.model.Owner
import org.springframework.data.mongodb.repository.MongoRepository

interface OwnerRepository : MongoRepository<Owner, String> {
    fun findByEmail(email: String): Owner?
    fun findByLastNameContainingIgnoreCase(lastName: String): List<Owner>
}
