package com.kirndoubleb.labs.service.external

import com.kirndoubleb.labs.domain.model.Appointment
import com.kirndoubleb.labs.exception.ExternalServiceException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.retry.Retry
import io.micrometer.observation.annotation.Observed
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

data class NotificationRequest(
    val appointmentId: String,
    val petId: String,
    val vetId: String,
    val scheduledAt: String,
    val type: String = "APPOINTMENT_REMINDER"
)

data class NotificationResponse(
    val notificationId: String,
    val status: String,
    val sent: Boolean
)

@Service
@Observed(name = "notification.service")
class NotificationService(
    @Qualifier("notificationRestClient")
    private val restClient: RestClient,
    @Qualifier("notificationServiceCircuitBreaker")
    private val circuitBreaker: CircuitBreaker,
    @Qualifier("notificationServiceRetry")
    private val retry: Retry
) {
    private val log = LoggerFactory.getLogger(NotificationService::class.java)

    fun sendAppointmentReminder(appointment: Appointment): Boolean {
        return try {
            val decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker) {
                Retry.decorateSupplier(retry) {
                    executeNotification(appointment)
                }.get()
            }
            decoratedSupplier.get()
        } catch (e: CallNotPermittedException) {
            log.warn("Circuit breaker is open for notification service")
            throw ExternalServiceException("notification", e)
        } catch (e: Exception) {
            log.error("Failed to send reminder for appointment: ${appointment.id}", e)
            throw ExternalServiceException("notification", e)
        }
    }

    private fun executeNotification(appointment: Appointment): Boolean {
        val request = NotificationRequest(
            appointmentId = appointment.id ?: "",
            petId = appointment.petId,
            vetId = appointment.vetId,
            scheduledAt = appointment.scheduledAt.toString()
        )

        val response = restClient.post()
            .uri("/api/notifications")
            .body(request)
            .retrieve()
            .body(NotificationResponse::class.java)

        return response?.sent ?: false
    }
}
