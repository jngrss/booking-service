package com.sample.booking.domain.exception

class ResourceNotFoundException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)
