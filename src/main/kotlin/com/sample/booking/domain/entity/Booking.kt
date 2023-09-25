package com.sample.booking.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "booking")
class Booking(
    var startTime: LocalDateTime,
    var endTime: LocalDateTime,
    var firstName: String,
    var lastName: String,

    @OneToOne
    @JoinTable(
        name = "booked_room",
        joinColumns = [JoinColumn(name = "booking_id")],
        inverseJoinColumns = [JoinColumn(name = "room_id")]
    )
    var bookedRoom: MeetingRoom,

    @OneToMany
    @JoinTable(
        name = "booked_equipment",
        joinColumns = [JoinColumn(name = "booking_id")],
        inverseJoinColumns = [JoinColumn(name = "equipment_id")]
    )
    var bookedEquipment: MutableSet<Equipment> = mutableSetOf()

) : BaseEntity<Long>()
