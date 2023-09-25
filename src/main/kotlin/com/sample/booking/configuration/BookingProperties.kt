package com.sample.booking.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.LocalTime

@ConfigurationProperties(prefix = "booking")
class BookingProperties(
    val allowedStartTime: LocalTime,
    val allowedEndTime: LocalTime,
    val purgeBookingsOlderThanMillis: String,
)
