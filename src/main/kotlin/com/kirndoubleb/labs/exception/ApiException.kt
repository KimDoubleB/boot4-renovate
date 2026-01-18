package com.kirndoubleb.labs.exception

import org.springframework.http.HttpStatus

open class ApiException(
    val status: HttpStatus,
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)

class ResourceNotFoundException(
    resourceType: String,
    resourceId: String
) : ApiException(
    status = HttpStatus.NOT_FOUND,
    message = "$resourceType not found with id: $resourceId"
)

class DuplicateResourceException(
    resourceType: String,
    field: String,
    value: String
) : ApiException(
    status = HttpStatus.CONFLICT,
    message = "$resourceType already exists with $field: $value"
)

class InvalidOperationException(
    message: String
) : ApiException(
    status = HttpStatus.BAD_REQUEST,
    message = message
)

class ExternalServiceException(
    serviceName: String,
    cause: Throwable? = null
) : ApiException(
    status = HttpStatus.SERVICE_UNAVAILABLE,
    message = "External service '$serviceName' is unavailable",
    cause = cause
)
