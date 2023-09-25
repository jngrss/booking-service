package com.sample.booking.service

import com.sample.booking.domain.entity.Booking
import com.sample.booking.domain.entity.Equipment
import com.sample.booking.domain.entity.MeetingRoom
import com.sample.booking.repository.BookingRepository
import com.sample.booking.repository.EquipmentRepository
import com.sample.booking.repository.MeetingRoomRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class BookingPurgeServiceTest {

    @Autowired
    lateinit var bookingPurgeService: BookingPurgeService

    @Autowired
    lateinit var bookingRepository: BookingRepository

    @Autowired
    lateinit var meetingRoomRepository: MeetingRoomRepository

    @Autowired
    lateinit var equipmentRepository: EquipmentRepository

    private lateinit var obsoleteBooking: Booking
    private lateinit var activeBooking: Booking

    @BeforeEach
    fun setUp() {
        createObsoleteBooking()
        createActiveBooking()
    }

    @AfterEach
    fun tearDown() {
        bookingRepository.deleteAll()
    }

    @Test
    fun `Should remove booking record older than set threshold`() {
        assertThat(bookingRepository.findAll()).hasSize(2)

        bookingPurgeService.purgeObsoleteBookings()

        val bookings = bookingRepository.findAll()

        assertThat(bookings).hasSize(1)
        assertThat(bookings[0].getId()).isEqualTo(activeBooking.getId())
    }

    private fun createObsoleteBooking() {
        val meetingRoom1 = meetingRoomRepository.save(MeetingRoom(name = "room 1"))
        val equipment1 = equipmentRepository.save(Equipment(name = "equipment 1"))
        obsoleteBooking = bookingRepository.save(
            Booking(
                startTime = LocalDateTime.now().minusMonths(1),
                endTime = LocalDateTime.now().minusMonths(1).plusHours(1),
                firstName = "name",
                lastName = "surname",
                bookedRoom = meetingRoom1,
                bookedEquipment = mutableSetOf(equipment1)
            )
        )
    }

    private fun createActiveBooking() {
        val meetingRoom2 = meetingRoomRepository.save(MeetingRoom(name = "room 2"))
        val equipment2 = equipmentRepository.save(Equipment(name = "equipment 2"))
        activeBooking = bookingRepository.saveAndFlush(
            Booking(
                startTime = LocalDateTime.now().plusDays(1),
                endTime = LocalDateTime.now().plusDays(1).plusHours(1),
                firstName = "name",
                lastName = "surname",
                bookedRoom = meetingRoom2,
                bookedEquipment = mutableSetOf(equipment2)
            )
        )
    }
}
