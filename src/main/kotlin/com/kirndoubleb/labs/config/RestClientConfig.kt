package com.kirndoubleb.labs.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {

    @Value("\${external.payment.url:http://localhost:8081}")
    private lateinit var paymentServiceUrl: String

    @Value("\${external.notification.url:http://localhost:8082}")
    private lateinit var notificationServiceUrl: String

    @Bean
    fun paymentRestClient(): RestClient {
        return RestClient.builder()
            .baseUrl(paymentServiceUrl)
            .requestFactory(createRequestFactory())
            .build()
    }

    @Bean
    fun notificationRestClient(): RestClient {
        return RestClient.builder()
            .baseUrl(notificationServiceUrl)
            .requestFactory(createRequestFactory())
            .build()
    }

    private fun createRequestFactory(): SimpleClientHttpRequestFactory {
        return SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(5000)
            setReadTimeout(5000)
        }
    }
}
