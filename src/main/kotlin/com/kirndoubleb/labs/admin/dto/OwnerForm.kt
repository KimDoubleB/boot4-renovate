package com.kirndoubleb.labs.admin.dto

import com.kirndoubleb.labs.domain.model.Owner
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class OwnerForm(
    @field:NotBlank(message = "이름을 입력해주세요")
    var firstName: String = "",

    @field:NotBlank(message = "성을 입력해주세요")
    var lastName: String = "",

    @field:NotBlank(message = "이메일을 입력해주세요")
    @field:Email(message = "올바른 이메일 형식을 입력해주세요")
    var email: String = "",

    @field:NotBlank(message = "전화번호를 입력해주세요")
    var phone: String = "",

    var address: String? = null
) {
    fun toEntity(): Owner = Owner(
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        address = address
    )

    companion object {
        fun from(owner: Owner): OwnerForm = OwnerForm(
            firstName = owner.firstName,
            lastName = owner.lastName,
            email = owner.email,
            phone = owner.phone,
            address = owner.address
        )
    }
}
