package com.sample.booking.repository

import com.sample.booking.domain.entity.MeetingRoom
import org.springframework.data.jpa.repository.JpaRepository

interface MeetingRoomRepository : JpaRepository<MeetingRoom, Long>
