package com.kirndoubleb.labs.admin.controller

import com.kirndoubleb.labs.admin.dto.AppointmentForm
import com.kirndoubleb.labs.domain.model.AppointmentStatus
import com.kirndoubleb.labs.service.AppointmentService
import com.kirndoubleb.labs.service.PetService
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
import java.time.ZoneId

@Controller
@RequestMapping("/admin/appointments")
class AdminAppointmentController(
    private val appointmentService: AppointmentService,
    private val petService: PetService,
    private val vetService: VetService
) {

    @GetMapping
    fun list(
        @PageableDefault(size = 10, sort = ["scheduledAt"], direction = Sort.Direction.DESC)
        pageable: Pageable,
        model: Model
    ): String {
        val appointments = appointmentService.findAll(pageable)
        model.addAttribute("appointments", appointments)
        model.addAttribute("pageTitle", "예약 관리")
        model.addAttribute("activeMenu", "appointments")
        return "admin/appointments/list"
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: String, model: Model): String {
        val appointment = appointmentService.findById(id)
        val pet = petService.findById(appointment.petId)
        val vet = vetService.findById(appointment.vetId)
        model.addAttribute("appointment", appointment)
        model.addAttribute("pet", pet)
        model.addAttribute("vet", vet)
        model.addAttribute("pageTitle", "예약 상세")
        model.addAttribute("activeMenu", "appointments")
        return "admin/appointments/detail"
    }

    @GetMapping("/new")
    fun showCreateForm(
        @RequestParam(required = false) petId: String?,
        @RequestParam(required = false) vetId: String?,
        model: Model
    ): String {
        val appointmentForm = AppointmentForm()
        if (petId != null) {
            appointmentForm.petId = petId
        }
        if (vetId != null) {
            appointmentForm.vetId = vetId
        }
        model.addAttribute("appointmentForm", appointmentForm)
        model.addAttribute("pets", petService.findAll(Pageable.unpaged()).content)
        model.addAttribute("vets", vetService.findAvailable())
        model.addAttribute("statuses", AppointmentStatus.entries)
        model.addAttribute("pageTitle", "예약 등록")
        model.addAttribute("activeMenu", "appointments")
        model.addAttribute("isNew", true)
        return "admin/appointments/form"
    }

    @PostMapping
    fun create(
        @Valid @ModelAttribute appointmentForm: AppointmentForm,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pets", petService.findAll(Pageable.unpaged()).content)
            model.addAttribute("vets", vetService.findAvailable())
            model.addAttribute("statuses", AppointmentStatus.entries)
            model.addAttribute("pageTitle", "예약 등록")
            model.addAttribute("activeMenu", "appointments")
            model.addAttribute("isNew", true)
            return "admin/appointments/form"
        }

        val appointment = appointmentService.create(appointmentForm.toEntity())
        redirectAttributes.addFlashAttribute("successMessage", "예약이 등록되었습니다.")
        return "redirect:/admin/appointments/${appointment.id}"
    }

    @GetMapping("/{id}/edit")
    fun showEditForm(@PathVariable id: String, model: Model): String {
        val appointment = appointmentService.findById(id)
        model.addAttribute("appointmentForm", AppointmentForm.from(appointment))
        model.addAttribute("appointmentId", id)
        model.addAttribute("pets", petService.findAll(Pageable.unpaged()).content)
        model.addAttribute("vets", vetService.findAll(Pageable.unpaged()).content)
        model.addAttribute("statuses", AppointmentStatus.entries)
        model.addAttribute("pageTitle", "예약 수정")
        model.addAttribute("activeMenu", "appointments")
        model.addAttribute("isNew", false)
        return "admin/appointments/form"
    }

    @PostMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @Valid @ModelAttribute appointmentForm: AppointmentForm,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("appointmentId", id)
            model.addAttribute("pets", petService.findAll(Pageable.unpaged()).content)
            model.addAttribute("vets", vetService.findAll(Pageable.unpaged()).content)
            model.addAttribute("statuses", AppointmentStatus.entries)
            model.addAttribute("pageTitle", "예약 수정")
            model.addAttribute("activeMenu", "appointments")
            model.addAttribute("isNew", false)
            return "admin/appointments/form"
        }

        appointmentService.update(id) { existing ->
            existing.copy(
                petId = appointmentForm.petId,
                vetId = appointmentForm.vetId,
                scheduledAt = appointmentForm.scheduledDateTime!!.atZone(ZoneId.systemDefault()).toInstant(),
                status = appointmentForm.status,
                reason = appointmentForm.reason
            )
        }
        redirectAttributes.addFlashAttribute("successMessage", "예약 정보가 수정되었습니다.")
        return "redirect:/admin/appointments/$id"
    }

    @PostMapping("/{id}/confirm")
    fun confirm(@PathVariable id: String, redirectAttributes: RedirectAttributes): String {
        appointmentService.confirm(id)
        redirectAttributes.addFlashAttribute("successMessage", "예약이 확정되었습니다.")
        return "redirect:/admin/appointments/$id"
    }

    @PostMapping("/{id}/cancel")
    fun cancel(@PathVariable id: String, redirectAttributes: RedirectAttributes): String {
        appointmentService.cancel(id)
        redirectAttributes.addFlashAttribute("successMessage", "예약이 취소되었습니다.")
        return "redirect:/admin/appointments/$id"
    }
}
