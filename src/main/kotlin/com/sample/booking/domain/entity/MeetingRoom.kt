package com.sample.booking.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "meeting_room")
class MeetingRoom(
    var name: String?
) : BaseEntity<Long>()
