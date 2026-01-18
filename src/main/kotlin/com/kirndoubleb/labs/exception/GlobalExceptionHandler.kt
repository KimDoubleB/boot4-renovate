package com.kirndoubleb.labs.exception

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import io.sentry.Sentry
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ApiException::class)
    fun handleApiException(
        ex: ApiException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn("API exception: ${ex.message}")

        return ResponseEntity
            .status(ex.status)
            .body(createErrorResponse(ex.status, ex.message, request))
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn("Resource not found: ${ex.message}")

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(createErrorResponse(HttpStatus.NOT_FOUND, ex.message, request))
    }

    @ExceptionHandler(CallNotPermittedException::class)
    fun handleCircuitBreakerException(
        ex: CallNotPermittedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error("Circuit breaker is open", ex)
        Sentry.captureException(ex)

        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(
                createErrorResponse(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Service temporarily unavailable. Please try again later.",
                    request
                )
            )
    }

    @ExceptionHandler(RequestNotPermitted::class)
    fun handleRateLimitException(
        ex: RequestNotPermitted,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn("Rate limit exceeded: ${ex.message}")

        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .body(
                createErrorResponse(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Rate limit exceeded. Please try again later.",
                    request
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error occurred", ex)
        Sentry.captureException(ex)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                createErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred",
                    request
                )
            )
    }

    private fun createErrorResponse(
        status: HttpStatus,
        message: String,
        request: HttpServletRequest
    ): ErrorResponse {
        return ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = request.requestURI,
            traceId = request.getHeader("X-Trace-Id")
        )
    }
}
