package com.kirndoubleb.labs.admin.controller

import com.kirndoubleb.labs.admin.dto.OwnerForm
import com.kirndoubleb.labs.service.OwnerService
import com.kirndoubleb.labs.service.PetService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin/owners")
class AdminOwnerController(
    private val ownerService: OwnerService,
    private val petService: PetService
) {

    @GetMapping
    fun list(
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable,
        model: Model
    ): String {
        val owners = ownerService.findAll(pageable)
        model.addAttribute("owners", owners)
        model.addAttribute("pageTitle", "보호자 관리")
        model.addAttribute("activeMenu", "owners")
        return "admin/owners/list"
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: String, model: Model): String {
        val owner = ownerService.findById(id)
        val pets = petService.findByOwnerId(id)
        model.addAttribute("owner", owner)
        model.addAttribute("pets", pets)
        model.addAttribute("pageTitle", "보호자 상세")
        model.addAttribute("activeMenu", "owners")
        return "admin/owners/detail"
    }

    @GetMapping("/new")
    fun showCreateForm(model: Model): String {
        model.addAttribute("ownerForm", OwnerForm())
        model.addAttribute("pageTitle", "보호자 등록")
        model.addAttribute("activeMenu", "owners")
        model.addAttribute("isNew", true)
        return "admin/owners/form"
    }

    @PostMapping
    fun create(
        @Valid @ModelAttribute ownerForm: OwnerForm,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "보호자 등록")
            model.addAttribute("activeMenu", "owners")
            model.addAttribute("isNew", true)
            return "admin/owners/form"
        }

        val owner = ownerService.create(ownerForm.toEntity())
        redirectAttributes.addFlashAttribute("successMessage", "보호자가 등록되었습니다.")
        return "redirect:/admin/owners/${owner.id}"
    }

    @GetMapping("/{id}/edit")
    fun showEditForm(@PathVariable id: String, model: Model): String {
        val owner = ownerService.findById(id)
        model.addAttribute("ownerForm", OwnerForm.from(owner))
        model.addAttribute("ownerId", id)
        model.addAttribute("pageTitle", "보호자 수정")
        model.addAttribute("activeMenu", "owners")
        model.addAttribute("isNew", false)
        return "admin/owners/form"
    }

    @PostMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @Valid @ModelAttribute ownerForm: OwnerForm,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ownerId", id)
            model.addAttribute("pageTitle", "보호자 수정")
            model.addAttribute("activeMenu", "owners")
            model.addAttribute("isNew", false)
            return "admin/owners/form"
        }

        ownerService.update(id) { existing ->
            existing.copy(
                firstName = ownerForm.firstName,
                lastName = ownerForm.lastName,
                email = ownerForm.email,
                phone = ownerForm.phone,
                address = ownerForm.address
            )
        }
        redirectAttributes.addFlashAttribute("successMessage", "보호자 정보가 수정되었습니다.")
        return "redirect:/admin/owners/$id"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: String, redirectAttributes: RedirectAttributes): String {
        ownerService.delete(id)
        redirectAttributes.addFlashAttribute("successMessage", "보호자가 삭제되었습니다.")
        return "redirect:/admin/owners"
    }
}
