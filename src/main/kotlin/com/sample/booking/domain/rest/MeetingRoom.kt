package com.sample.booking.domain.rest

data class MeetingRoom(
    val id: Long,
    val name: String,
) {
    companion object {
        fun mapFrom(input: com.sample.booking.domain.entity.MeetingRoom): MeetingRoom {
            return MeetingRoom(
                id = input.getId()!!,
                name = input.name!!,
            )
        }
    }
}
