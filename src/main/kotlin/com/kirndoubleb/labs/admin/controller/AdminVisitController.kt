package com.kirndoubleb.labs.admin.controller

import com.kirndoubleb.labs.admin.dto.VisitForm
import com.kirndoubleb.labs.domain.model.PaymentStatus
import com.kirndoubleb.labs.service.PetService
import com.kirndoubleb.labs.service.VetService
import com.kirndoubleb.labs.service.VisitService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.Instant
import java.time.ZoneId

@Controller
@RequestMapping("/admin/visits")
class AdminVisitController(
    private val visitService: VisitService,
    private val petService: PetService,
    private val vetService: VetService
) {

    @GetMapping
    fun list(
        @PageableDefault(size = 10, sort = ["visitDate"], direction = Sort.Direction.DESC)
        pageable: Pageable,
        model: Model
    ): String {
        val visits = visitService.findAll(pageable)
        model.addAttribute("visits", visits)
        model.addAttribute("pageTitle", "진료 기록")
        model.addAttribute("activeMenu", "visits")
        return "admin/visits/list"
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: String, model: Model): String {
        val visit = visitService.findById(id)
        val pet = petService.findById(visit.petId)
        val vet = vetService.findById(visit.vetId)
        model.addAttribute("visit", visit)
        model.addAttribute("pet", pet)
        model.addAttribute("vet", vet)
        model.addAttribute("pageTitle", "진료 상세")
        model.addAttribute("activeMenu", "visits")
        return "admin/visits/detail"
    }

    @GetMapping("/new")
    fun showCreateForm(
        @RequestParam(required = false) petId: String?,
        model: Model
    ): String {
        val visitForm = VisitForm()
        if (petId != null) {
            visitForm.petId = petId
        }
        model.addAttribute("visitForm", visitForm)
        model.addAttribute("pets", petService.findAll(Pageable.unpaged()).content)
        model.addAttribute("vets", vetService.findAvailable())
        model.addAttribute("paymentStatuses", PaymentStatus.entries)
        model.addAttribute("pageTitle", "진료 기록 등록")
        model.addAttribute("activeMenu", "visits")
        model.addAttribute("isNew", true)
        return "admin/visits/form"
    }

    @PostMapping
    fun create(
        @Valid @ModelAttribute visitForm: VisitForm,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pets", petService.findAll(Pageable.unpaged()).content)
            model.addAttribute("vets", vetService.findAvailable())
            model.addAttribute("paymentStatuses", PaymentStatus.entries)
            model.addAttribute("pageTitle", "진료 기록 등록")
            model.addAttribute("activeMenu", "visits")
            model.addAttribute("isNew", true)
            return "admin/visits/form"
        }

        val visit = visitService.create(visitForm.toEntity())
        redirectAttributes.addFlashAttribute("successMessage", "진료 기록이 등록되었습니다.")
        return "redirect:/admin/visits/${visit.id}"
    }

    @GetMapping("/{id}/edit")
    fun showEditForm(@PathVariable id: String, model: Model): String {
        val visit = visitService.findById(id)
        model.addAttribute("visitForm", VisitForm.from(visit))
        model.addAttribute("visitId", id)
        model.addAttribute("pets", petService.findAll(Pageable.unpaged()).content)
        model.addAttribute("vets", vetService.findAll(Pageable.unpaged()).content)
        model.addAttribute("paymentStatuses", PaymentStatus.entries)
        model.addAttribute("pageTitle", "진료 기록 수정")
        model.addAttribute("activeMenu", "visits")
        model.addAttribute("isNew", false)
        return "admin/visits/form"
    }

    @PostMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @Valid @ModelAttribute visitForm: VisitForm,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("visitId", id)
            model.addAttribute("pets", petService.findAll(Pageable.unpaged()).content)
            model.addAttribute("vets", vetService.findAll(Pageable.unpaged()).content)
            model.addAttribute("paymentStatuses", PaymentStatus.entries)
            model.addAttribute("pageTitle", "진료 기록 수정")
            model.addAttribute("activeMenu", "visits")
            model.addAttribute("isNew", false)
            return "admin/visits/form"
        }

        visitService.update(id) { existing ->
            existing.copy(
                petId = visitForm.petId,
                vetId = visitForm.vetId,
                visitDate = visitForm.visitDateTime?.atZone(ZoneId.systemDefault())?.toInstant() ?: Instant.now(),
                diagnosis = visitForm.diagnosis,
                treatment = visitForm.treatment,
                notes = visitForm.notes,
                cost = visitForm.cost,
                paymentStatus = visitForm.paymentStatus
            )
        }
        redirectAttributes.addFlashAttribute("successMessage", "진료 기록이 수정되었습니다.")
        return "redirect:/admin/visits/$id"
    }
}
