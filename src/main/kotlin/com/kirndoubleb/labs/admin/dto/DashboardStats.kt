package com.kirndoubleb.labs.admin.dto

data class DashboardStats(
    val totalOwners: Long,
    val totalPets: Long,
    val totalVets: Long,
    val totalVisits: Long,
    val totalAppointments: Long,
    val pendingAppointments: Long,
    val availableVets: Int
)
