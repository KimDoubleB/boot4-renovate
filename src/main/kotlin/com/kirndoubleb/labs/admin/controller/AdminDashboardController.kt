package com.kirndoubleb.labs.admin.controller

import com.kirndoubleb.labs.admin.dto.DashboardStats
import com.kirndoubleb.labs.domain.model.AppointmentStatus
import com.kirndoubleb.labs.domain.repository.AppointmentRepository
import com.kirndoubleb.labs.domain.repository.OwnerRepository
import com.kirndoubleb.labs.domain.repository.PetRepository
import com.kirndoubleb.labs.domain.repository.VetRepository
import com.kirndoubleb.labs.domain.repository.VisitRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class AdminDashboardController(
    private val ownerRepository: OwnerRepository,
    private val petRepository: PetRepository,
    private val vetRepository: VetRepository,
    private val visitRepository: VisitRepository,
    private val appointmentRepository: AppointmentRepository
) {

    @GetMapping
    fun dashboard(model: Model): String {
        val stats = DashboardStats(
            totalOwners = ownerRepository.count(),
            totalPets = petRepository.count(),
            totalVets = vetRepository.count(),
            totalVisits = visitRepository.count(),
            totalAppointments = appointmentRepository.count(),
            pendingAppointments = appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED).size.toLong(),
            availableVets = vetRepository.findByAvailableTrue().size
        )

        model.addAttribute("stats", stats)
        model.addAttribute("pageTitle", "대시보드")
        model.addAttribute("activeMenu", "dashboard")

        return "admin/dashboard"
    }
}
