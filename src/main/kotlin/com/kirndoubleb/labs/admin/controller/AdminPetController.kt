package com.kirndoubleb.labs.admin.controller

import com.kirndoubleb.labs.admin.dto.PetForm
import com.kirndoubleb.labs.domain.model.Species
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
@RequestMapping("/admin/pets")
class AdminPetController(
    private val petService: PetService,
    private val ownerService: OwnerService
) {

    @GetMapping
    fun list(
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable,
        model: Model
    ): String {
        val pets = petService.findAll(pageable)
        model.addAttribute("pets", pets)
        model.addAttribute("pageTitle", "반려동물 관리")
        model.addAttribute("activeMenu", "pets")
        return "admin/pets/list"
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: String, model: Model): String {
        val pet = petService.findById(id)
        val owner = ownerService.findById(pet.ownerId)
        model.addAttribute("pet", pet)
        model.addAttribute("owner", owner)
        model.addAttribute("pageTitle", "반려동물 상세")
        model.addAttribute("activeMenu", "pets")
        return "admin/pets/detail"
    }

    @GetMapping("/new")
    fun showCreateForm(
        @RequestParam(required = false) ownerId: String?,
        model: Model
    ): String {
        val petForm = PetForm()
        if (ownerId != null) {
            petForm.ownerId = ownerId
        }
        model.addAttribute("petForm", petForm)
        model.addAttribute("species", Species.entries)
        model.addAttribute("owners", ownerService.findAll(Pageable.unpaged()).content)
        model.addAttribute("pageTitle", "반려동물 등록")
        model.addAttribute("activeMenu", "pets")
        model.addAttribute("isNew", true)
        return "admin/pets/form"
    }

    @PostMapping
    fun create(
        @Valid @ModelAttribute petForm: PetForm,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("species", Species.entries)
            model.addAttribute("owners", ownerService.findAll(Pageable.unpaged()).content)
            model.addAttribute("pageTitle", "반려동물 등록")
            model.addAttribute("activeMenu", "pets")
            model.addAttribute("isNew", true)
            return "admin/pets/form"
        }

        val pet = petService.create(petForm.toEntity())
        redirectAttributes.addFlashAttribute("successMessage", "반려동물이 등록되었습니다.")
        return "redirect:/admin/pets/${pet.id}"
    }

    @GetMapping("/{id}/edit")
    fun showEditForm(@PathVariable id: String, model: Model): String {
        val pet = petService.findById(id)
        model.addAttribute("petForm", PetForm.from(pet))
        model.addAttribute("petId", id)
        model.addAttribute("species", Species.entries)
        model.addAttribute("owners", ownerService.findAll(Pageable.unpaged()).content)
        model.addAttribute("pageTitle", "반려동물 수정")
        model.addAttribute("activeMenu", "pets")
        model.addAttribute("isNew", false)
        return "admin/pets/form"
    }

    @PostMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @Valid @ModelAttribute petForm: PetForm,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("petId", id)
            model.addAttribute("species", Species.entries)
            model.addAttribute("owners", ownerService.findAll(Pageable.unpaged()).content)
            model.addAttribute("pageTitle", "반려동물 수정")
            model.addAttribute("activeMenu", "pets")
            model.addAttribute("isNew", false)
            return "admin/pets/form"
        }

        petService.update(id) { existing ->
            existing.copy(
                name = petForm.name,
                species = petForm.species!!,
                breed = petForm.breed,
                birthDate = petForm.birthDate,
                ownerId = petForm.ownerId
            )
        }
        redirectAttributes.addFlashAttribute("successMessage", "반려동물 정보가 수정되었습니다.")
        return "redirect:/admin/pets/$id"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: String, redirectAttributes: RedirectAttributes): String {
        petService.delete(id)
        redirectAttributes.addFlashAttribute("successMessage", "반려동물이 삭제되었습니다.")
        return "redirect:/admin/pets"
    }
}
