package com.kirndoubleb.labs.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration(proxyBeanMethods = false)
class WireMockConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    fun paymentWireMockServer(): WireMockServer {
        return WireMockServer(wireMockConfig().dynamicPort())
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    fun notificationWireMockServer(): WireMockServer {
        return WireMockServer(wireMockConfig().dynamicPort())
    }
}
