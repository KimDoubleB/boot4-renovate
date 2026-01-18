package com.kirndoubleb.labs.config

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class Resilience4jConfig {

    @Bean
    fun circuitBreakerRegistry(): CircuitBreakerRegistry {
        val defaultConfig = CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .failureRateThreshold(50f)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .permittedNumberOfCallsInHalfOpenState(3)
            .build()

        return CircuitBreakerRegistry.of(defaultConfig)
    }

    @Bean
    fun paymentServiceCircuitBreaker(registry: CircuitBreakerRegistry): CircuitBreaker {
        val config = CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .failureRateThreshold(50f)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .permittedNumberOfCallsInHalfOpenState(3)
            .build()

        return registry.circuitBreaker("paymentService", config)
    }

    @Bean
    fun notificationServiceCircuitBreaker(registry: CircuitBreakerRegistry): CircuitBreaker {
        val config = CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .failureRateThreshold(50f)
            .waitDurationInOpenState(Duration.ofSeconds(5))
            .permittedNumberOfCallsInHalfOpenState(3)
            .build()

        return registry.circuitBreaker("notificationService", config)
    }

    @Bean
    fun retryRegistry(): RetryRegistry {
        val defaultConfig = RetryConfig.custom<Any>()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(1))
            .build()

        return RetryRegistry.of(defaultConfig)
    }

    @Bean
    fun notificationServiceRetry(registry: RetryRegistry): Retry {
        val config = RetryConfig.custom<Any>()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(1))
            .retryExceptions(Exception::class.java)
            .build()

        return registry.retry("notificationService", config)
    }

    @Bean
    fun rateLimiterRegistry(): RateLimiterRegistry {
        val defaultConfig = RateLimiterConfig.custom()
            .limitForPeriod(5)
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .timeoutDuration(Duration.ofMillis(500))
            .build()

        return RateLimiterRegistry.of(defaultConfig)
    }

    @Bean
    fun createEndpointsRateLimiter(registry: RateLimiterRegistry): RateLimiter {
        return registry.rateLimiter("createEndpoints")
    }
}
