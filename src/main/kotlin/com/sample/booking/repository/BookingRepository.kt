package com.sample.booking.repository

import com.sample.booking.domain.entity.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface BookingRepository : JpaRepository<Booking, Long> {

    @Query(
        "select b from Booking b where " +
                "(:startDateTime between b.startTime and b.endTime " +
                "or " +
                ":endDateTime between b.startTime and b.endTime)"
    )
    fun findAllOverlappingByTime(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<Booking>

    @Modifying
    @Query("delete from Booking b where b.endTime <= :endTimeThreshold")
    fun deleteAllByEndTimeOlderThan(endTimeThreshold: LocalDateTime)
}
