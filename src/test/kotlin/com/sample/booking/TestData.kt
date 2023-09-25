package com.sample.booking

import com.sample.booking.domain.rest.BookingRequest
import com.sample.booking.domain.rest.BookingResponse
import com.sample.booking.domain.rest.Equipment
import com.sample.booking.domain.rest.MeetingRoom
import java.time.LocalDateTime

object TestData {

    fun getSampleBookingRequest(roomId: Long, equipmentIds: List<Long>) = BookingRequest(
        startTime = LocalDateTime.now().withHour(11).withMinute(0).withSecond(0).withNano(0),
        endTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0).withNano(0),
        firstName = "name",
        lastName = "surname",
        meetingRoomId = roomId,
        equipmentIds = equipmentIds
    )

    fun getSampleBookingResponse(): BookingResponse {
        val meetingRoom = MeetingRoom(id = 1, name = "room")
        val equipment = Equipment(id = 1, name = "equipment")
        return BookingResponse(
            id = 1,
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now(),
            firstName = "name",
            lastName = "surname",
            meetingRoom = meetingRoom,
            equipment = listOf(equipment)
        )
    }
}