package com.sample.booking.domain.rest

import com.sample.booking.domain.entity.Booking
import java.time.LocalDateTime

data class BookingResponse(
    val id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val firstName: String,
    val lastName: String,
    val meetingRoom: MeetingRoom,
    val equipment: List<Equipment>? = null
) {
    companion object {
        fun mapFrom(input: Booking): BookingResponse {
            return BookingResponse(
                id = input.getId()!!,
                startTime = input.startTime,
                endTime = input.endTime,
                firstName = input.firstName,
                lastName = input.lastName,
                meetingRoom = MeetingRoom.mapFrom(input.bookedRoom),
                equipment = input.bookedEquipment
                    .map { eq -> Equipment.mapFrom(eq) }
                    .toList()
            )
        }
    }
}
