package com.sample.booking.domain.rest

import java.time.LocalDateTime

data class BookingRequest(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val firstName: String,
    val lastName: String,
    val meetingRoomId: Long,
    val equipmentIds: List<Long> = emptyList()
)
