package com.kirndoubleb.labs.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
class RequestLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (shouldSkip(request)) {
            filterChain.doFilter(request, response)
            return
        }

        val wrappedRequest = ContentCachingRequestWrapper(request, 10240)
        val wrappedResponse = ContentCachingResponseWrapper(response)
        val startTime = System.currentTimeMillis()

        try {
            logRequest(wrappedRequest)
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            val duration = System.currentTimeMillis() - startTime
            logResponse(wrappedRequest, wrappedResponse, duration)
            wrappedResponse.copyBodyToResponse()
        }
    }

    private fun shouldSkip(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.startsWith("/actuator") ||
                path.startsWith("/health") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".ico")
    }

    private fun logRequest(request: ContentCachingRequestWrapper) {
        val method = request.method
        val uri = request.requestURI
        val queryString = request.queryString?.let { "?$it" } ?: ""
        val contentType = request.contentType ?: "-"

        log.info(">>> {} {}{} [Content-Type: {}]", method, uri, queryString, contentType)

        if (log.isDebugEnabled) {
            logRequestHeaders(request)
            logRequestBody(request)
        }
    }

    private fun logRequestHeaders(request: ContentCachingRequestWrapper) {
        val headers = request.headerNames.toList()
            .filter { !it.equals("authorization", ignoreCase = true) }
            .associateWith { request.getHeader(it) }

        if (headers.isNotEmpty()) {
            log.debug(">>> Headers: {}", headers)
        }
    }

    private fun logRequestBody(request: ContentCachingRequestWrapper) {
        val content = request.contentAsByteArray
        if (content.isNotEmpty()) {
            val body = String(content, Charsets.UTF_8).take(1000)
            log.debug(">>> Body: {}", body)
        }
    }

    private fun logResponse(
        request: ContentCachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        duration: Long
    ) {
        val method = request.method
        val uri = request.requestURI
        val status = response.status
        val statusText = getStatusText(status)

        log.info("<<< {} {} - {} {} ({}ms)", method, uri, status, statusText, duration)

        if (log.isDebugEnabled && status >= 400) {
            logResponseBody(response)
        }
    }

    private fun logResponseBody(response: ContentCachingResponseWrapper) {
        val content = response.contentAsByteArray
        if (content.isNotEmpty()) {
            val body = String(content, Charsets.UTF_8).take(1000)
            log.debug("<<< Body: {}", body)
        }
    }

    private fun getStatusText(status: Int): String = when (status) {
        200 -> "OK"
        201 -> "Created"
        204 -> "No Content"
        400 -> "Bad Request"
        401 -> "Unauthorized"
        403 -> "Forbidden"
        404 -> "Not Found"
        429 -> "Too Many Requests"
        500 -> "Internal Server Error"
        503 -> "Service Unavailable"
        else -> ""
    }
}
