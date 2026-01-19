package com.kirndoubleb.labs.admin.dto

import com.kirndoubleb.labs.domain.model.Vet
import jakarta.validation.constraints.NotBlank

data class VetForm(
    @field:NotBlank(message = "이름을 입력해주세요")
    var firstName: String = "",

    @field:NotBlank(message = "성을 입력해주세요")
    var lastName: String = "",

    @field:NotBlank(message = "면허 번호를 입력해주세요")
    var licenseNumber: String = "",

    var specialties: String = "",

    var available: Boolean = true
) {
    fun toEntity(): Vet = Vet(
        firstName = firstName,
        lastName = lastName,
        licenseNumber = licenseNumber,
        specialties = specialties.split(",").map { it.trim() }.filter { it.isNotBlank() },
        available = available
    )

    companion object {
        fun from(vet: Vet): VetForm = VetForm(
            firstName = vet.firstName,
            lastName = vet.lastName,
            licenseNumber = vet.licenseNumber,
            specialties = vet.specialties.joinToString(", "),
            available = vet.available
        )
    }
}
