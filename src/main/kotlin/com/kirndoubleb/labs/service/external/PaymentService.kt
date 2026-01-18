package com.kirndoubleb.labs.service.external

import com.kirndoubleb.labs.exception.ExternalServiceException
import com.kirndoubleb.labs.metrics.PetClinicMetrics
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.micrometer.observation.annotation.Observed
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.math.BigDecimal

data class PaymentRequest(
    val visitId: String,
    val amount: BigDecimal
)

data class PaymentResponse(
    val transactionId: String,
    val status: String,
    val success: Boolean
)

@Service
@Observed(name = "payment.service")
class PaymentService(
    @Qualifier("paymentRestClient")
    private val restClient: RestClient,
    @Qualifier("paymentServiceCircuitBreaker")
    private val circuitBreaker: CircuitBreaker,
    private val metrics: PetClinicMetrics
) {
    private val log = LoggerFactory.getLogger(PaymentService::class.java)

    fun processPayment(visitId: String, amount: BigDecimal): Boolean {
        metrics.incrementPaymentAttempts()

        return try {
            circuitBreaker.executeSupplier {
                executePayment(visitId, amount)
            }
        } catch (e: CallNotPermittedException) {
            log.warn("Circuit breaker is open for payment service")
            metrics.incrementPaymentFailures()
            throw ExternalServiceException("payment", e)
        } catch (e: Exception) {
            log.error("Payment processing failed for visit: $visitId", e)
            metrics.incrementPaymentFailures()
            false
        }
    }

    private fun executePayment(visitId: String, amount: BigDecimal): Boolean {
        val request = PaymentRequest(visitId, amount)

        val response = restClient.post()
            .uri("/api/payments")
            .body(request)
            .retrieve()
            .body(PaymentResponse::class.java)

        return response?.success ?: false
    }
}
