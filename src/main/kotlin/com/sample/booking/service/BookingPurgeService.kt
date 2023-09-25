package com.sample.booking.service

import com.sample.booking.configuration.BookingProperties
import com.sample.booking.repository.BookingRepository
import com.sample.booking.util.logger
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class BookingPurgeService(
    val bookingRepository: BookingRepository,
    val bookingProperties: BookingProperties,
) {

    private val logger = logger<BookingPurgeService>()

    /**
     * Deletes booking records having end_time property less than or equal to set threshold.
     * Booked resources (meeting room, equipment) having end_time in past will be removed
     * from repository.
     */
    @Scheduled(fixedDelayString = "\${booking.purgeBookingsOlderThanMillis}", initialDelay = 60000)
    @Transactional
    fun purgeObsoleteBookings() {
        val endTimeThreshold =
            LocalDateTime.now().minus(bookingProperties.purgeBookingsOlderThanMillis.toLong(), ChronoUnit.MILLIS)
        logger.info("Deleting booking records with end_time <= than $endTimeThreshold")
        bookingRepository.deleteAllByEndTimeOlderThan(endTimeThreshold)
    }
}
