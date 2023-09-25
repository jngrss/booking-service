package com.sample.booking.service

import com.sample.booking.TestData.getSampleBookingRequest
import com.sample.booking.domain.entity.Booking
import com.sample.booking.domain.entity.Equipment
import com.sample.booking.domain.entity.MeetingRoom
import com.sample.booking.domain.exception.ResourceNotFoundException
import com.sample.booking.repository.BookingRepository
import com.sample.booking.repository.EquipmentRepository
import com.sample.booking.repository.MeetingRoomRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class BookingServiceTest {

    @Autowired
    lateinit var bookingService: BookingService

    @Autowired
    lateinit var bookingRepository: BookingRepository

    @Autowired
    lateinit var meetingRoomRepository: MeetingRoomRepository

    @Autowired
    lateinit var equipmentRepository: EquipmentRepository

    @AfterEach
    fun tearDown() {
        bookingRepository.deleteAll()
    }

    @Test
    fun `Should get list of existing bookings`() {
        val meetingRoom = meetingRoomRepository.save(MeetingRoom(name = "room"))
        val equipment1 = equipmentRepository.save(Equipment(name = "equipment 1"))
        val equipment2 = equipmentRepository.save(Equipment(name = "equipment 2"))

        val booking = bookingRepository.save(
            Booking(
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now().plusHours(1),
                firstName = "name",
                lastName = "surname",
                bookedRoom = meetingRoom,
                bookedEquipment = mutableSetOf(equipment1, equipment2)
            )
        )

        val bookings = bookingService.getBookings()

        assertThat(bookings).hasSize(1)
        assertThat(bookings[0].startTime).isEqualTo(booking.startTime)
        assertThat(bookings[0].endTime).isEqualTo(booking.endTime)
        assertThat(bookings[0].meetingRoom.id).isEqualTo(meetingRoom.getId()!!)
        assertThat(bookings[0].equipment!!.map { it.id }).containsAll(
            listOf(
                equipment1.getId()!!,
                equipment2.getId()!!
            )
        )
    }

    @Test
    fun `Should successfully create new booking`() {
        val request = getSampleBookingRequest(10, listOf(20, 30))
        val response = bookingService.createBooking(request)

        assertThat(response.startTime).isEqualTo(request.startTime)
        assertThat(response.endTime).isEqualTo(request.endTime)
        assertThat(response.meetingRoom.id).isEqualTo(request.meetingRoomId)
        assertThat(response.equipment!!.map { it.id }).containsAll(request.equipmentIds)
    }

    @Test
    fun `Should not create booking with start time outside allowed hours`() {
        val request = getSampleBookingRequest(10, emptyList())
            .copy(startTime = LocalDateTime.now().withHour(3))

        assertThatThrownBy { bookingService.createBooking(request) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Start time not in allowed range")
    }

    @Test
    fun `Should not create booking with end time outside allowed hours`() {
        val request = getSampleBookingRequest(10, emptyList())
            .copy(endTime = LocalDateTime.now().withHour(23))

        assertThatThrownBy { bookingService.createBooking(request) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("End time not in allowed range")
    }

    @Test
    fun `Should not create booking with end time preceding start time`() {
        val request = getSampleBookingRequest(10, emptyList())
            .copy(
                startTime = LocalDateTime.now().withHour(15),
                endTime = LocalDateTime.now().withHour(14)
            )

        assertThatThrownBy { bookingService.createBooking(request) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Start time must precede end time")
    }

    @Test
    fun `Should not create booking exceeding day span`() {
        val request = getSampleBookingRequest(10, emptyList())
            .copy(
                startTime = LocalDateTime.now().withHour(11),
                endTime = LocalDateTime.now().withHour(12).plusDays(1)
            )

        assertThatThrownBy { bookingService.createBooking(request) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Booking time exceeds allowed range")
    }

    @Test
    fun `Should successfully update existing booking`() {
        val request = getSampleBookingRequest(10, listOf(10, 20))
        val initialBooking = bookingService.createBooking(request)

        val updateRequest = request.copy(meetingRoomId = 20, equipmentIds = listOf(30))
        val updatedBooking = bookingService.updateBooking(initialBooking.id, updateRequest)

        assertThat(updatedBooking.meetingRoom.id).isEqualTo(updateRequest.meetingRoomId)
        assertThat(updatedBooking.equipment!!.map { it.id }).containsAll(updateRequest.equipmentIds)
    }

    @Test
    fun `Should throw exception requesting booking update with non-existent room`() {
        val request = getSampleBookingRequest(10, listOf(10, 20))
        val initialBooking = bookingService.createBooking(request)

        val updateRequest = request.copy(meetingRoomId = 666, equipmentIds = listOf(30))

        assertThatThrownBy { bookingService.updateBooking(initialBooking.id, updateRequest) }
            .isInstanceOf(ResourceNotFoundException::class.java)
            .hasMessage("No such room exists")
    }

    @Test
    fun `Should throw exception requesting booking end time update outside allowed hours`() {
        val request = getSampleBookingRequest(10, listOf(10, 20))
        val initialBooking = bookingService.createBooking(request)

        val updateRequest = request.copy(endTime = LocalDateTime.now().withHour(23))

        assertThatThrownBy { bookingService.updateBooking(initialBooking.id, updateRequest) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("End time not in allowed range")
    }

    @Test
    fun `Should successfully delete booking`() {
        val meetingRoom = meetingRoomRepository.save(MeetingRoom(name = "room"))
        val equipment1 = equipmentRepository.save(Equipment(name = "equipment 1"))
        val equipment2 = equipmentRepository.save(Equipment(name = "equipment 2"))

        val booking = bookingRepository.save(
            Booking(
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now().plusHours(1),
                firstName = "name",
                lastName = "surname",
                bookedRoom = meetingRoom,
                bookedEquipment = mutableSetOf(equipment1, equipment2)
            )
        )

        assertThat(bookingService.getBookings()).hasSize(1)
        assertThat(booking.getId()).isNotNull()

        bookingService.cancelBooking(booking.getId()!!)

        assertThat(bookingService.getBookings()).hasSize(0)
    }
}
