package com.kirndoubleb.labs.admin.controller

import com.kirndoubleb.labs.admin.dto.VetForm
import com.kirndoubleb.labs.service.VetService
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
@RequestMapping("/admin/vets")
class AdminVetController(
    private val vetService: VetService
) {

    @GetMapping
    fun list(
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable,
        model: Model
    ): String {
        val vets = vetService.findAll(pageable)
        model.addAttribute("vets", vets)
        model.addAttribute("pageTitle", "수의사 관리")
        model.addAttribute("activeMenu", "vets")
        return "admin/vets/list"
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: String, model: Model): String {
        val vet = vetService.findById(id)
        model.addAttribute("vet", vet)
        model.addAttribute("pageTitle", "수의사 상세")
        model.addAttribute("activeMenu", "vets")
        return "admin/vets/detail"
    }

    @GetMapping("/new")
    fun showCreateForm(model: Model): String {
        model.addAttribute("vetForm", VetForm())
        model.addAttribute("pageTitle", "수의사 등록")
        model.addAttribute("activeMenu", "vets")
        model.addAttribute("isNew", true)
        return "admin/vets/form"
    }

    @PostMapping
    fun create(
        @Valid @ModelAttribute vetForm: VetForm,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "수의사 등록")
            model.addAttribute("activeMenu", "vets")
            model.addAttribute("isNew", true)
            return "admin/vets/form"
        }

        val vet = vetService.create(vetForm.toEntity())
        redirectAttributes.addFlashAttribute("successMessage", "수의사가 등록되었습니다.")
        return "redirect:/admin/vets/${vet.id}"
    }

    @GetMapping("/{id}/edit")
    fun showEditForm(@PathVariable id: String, model: Model): String {
        val vet = vetService.findById(id)
        model.addAttribute("vetForm", VetForm.from(vet))
        model.addAttribute("vetId", id)
        model.addAttribute("pageTitle", "수의사 수정")
        model.addAttribute("activeMenu", "vets")
        model.addAttribute("isNew", false)
        return "admin/vets/form"
    }

    @PostMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @Valid @ModelAttribute vetForm: VetForm,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vetId", id)
            model.addAttribute("pageTitle", "수의사 수정")
            model.addAttribute("activeMenu", "vets")
            model.addAttribute("isNew", false)
            return "admin/vets/form"
        }

        vetService.update(id) { existing ->
            existing.copy(
                firstName = vetForm.firstName,
                lastName = vetForm.lastName,
                licenseNumber = vetForm.licenseNumber,
                specialties = vetForm.specialties.split(",").map { it.trim() }.filter { it.isNotBlank() },
                available = vetForm.available
            )
        }
        redirectAttributes.addFlashAttribute("successMessage", "수의사 정보가 수정되었습니다.")
        return "redirect:/admin/vets/$id"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: String, redirectAttributes: RedirectAttributes): String {
        vetService.delete(id)
        redirectAttributes.addFlashAttribute("successMessage", "수의사가 삭제되었습니다.")
        return "redirect:/admin/vets"
    }
}
