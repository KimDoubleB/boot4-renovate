package com.kirndoubleb.labs.integration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.kirndoubleb.labs.config.TestContainersConfig
import com.kirndoubleb.labs.config.WireMockConfig
import com.kirndoubleb.labs.service.external.PaymentService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfig::class, WireMockConfig::class)
@ActiveProfiles("test")
class PaymentServiceIntegrationTest {

    @Autowired
    private lateinit var paymentService: PaymentService

    @Autowired
    @Qualifier("paymentWireMockServer")
    private lateinit var paymentWireMock: WireMockServer

    @BeforeEach
    fun setup() {
        paymentWireMock.resetAll()
    }

    @Test
    fun `should process payment successfully`() {
        paymentWireMock.stubFor(
            post(urlEqualTo("/api/payments"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                            {
                                "transactionId": "txn-123",
                                "status": "SUCCESS",
                                "success": true
                            }
                        """.trimIndent())
                )
        )

        val result = paymentService.processPayment("visit-123", BigDecimal("50.00"))

        assertEquals(true, result)
    }

    @Test
    fun `should handle payment failure`() {
        paymentWireMock.stubFor(
            post(urlEqualTo("/api/payments"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                            {
                                "transactionId": "txn-456",
                                "status": "FAILED",
                                "success": false
                            }
                        """.trimIndent())
                )
        )

        val result = paymentService.processPayment("visit-456", BigDecimal("100.00"))

        assertEquals(false, result)
    }

    @Test
    fun `should handle service unavailable gracefully`() {
        paymentWireMock.stubFor(
            post(urlEqualTo("/api/payments"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                )
        )

        val result = paymentService.processPayment("visit-789", BigDecimal("75.00"))

        assertEquals(false, result)
    }
}
