package com.sample.booking.service

import com.sample.booking.configuration.BookingProperties
import com.sample.booking.domain.entity.Booking
import com.sample.booking.domain.entity.MeetingRoom
import com.sample.booking.domain.exception.ResourceNotFoundException
import com.sample.booking.domain.rest.BookingRequest
import com.sample.booking.domain.rest.BookingResponse
import com.sample.booking.repository.BookingRepository
import com.sample.booking.repository.EquipmentRepository
import com.sample.booking.repository.MeetingRoomRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class BookingService(
    val bookingRepository: BookingRepository,
    val meetingRoomRepository: MeetingRoomRepository,
    val equipmentRepository: EquipmentRepository,
    val properties: BookingProperties,
) {

    /**
     * Returns list of existing bookings from repository.
     * @return list of existing bookings
     */
    @Transactional
    fun getBookings(): List<BookingResponse> {
        return bookingRepository.findAll().map { booking -> BookingResponse.mapFrom(booking) }
    }

    /**
     * Creates new booking in repository if all conditions are met:
     * - booking time falls within allowed range (start, end times, time span)
     * - requested room is available
     * - requested equipment is available
     *
     * @param bookingRequest booking details
     * @return created booking details
     * @throws IllegalStateException requested times are invalid
     * @throws IllegalArgumentException requested resource(s) unavailable
     * @throws ResourceNotFoundException resource does not exist
     */
    @Transactional
    fun createBooking(bookingRequest: BookingRequest): BookingResponse {
        checkRequestContainsValidTimesOrThrow(bookingRequest)

        val requestedRoom = meetingRoomRepository.findById(bookingRequest.meetingRoomId)
            .orElseThrow { ResourceNotFoundException("No such room exists") }

        val existingBookings =
            bookingRepository.findAllOverlappingByTime(bookingRequest.startTime, bookingRequest.endTime)
        checkResourcesAvailabilityOrThrow(existingBookings, requestedRoom, bookingRequest)

        val requestedEquipment = equipmentRepository.findAllById(bookingRequest.equipmentIds)
            .toMutableSet()

        val booking = Booking(
            startTime = bookingRequest.startTime,
            endTime = bookingRequest.endTime,
            firstName = bookingRequest.firstName,
            lastName = bookingRequest.lastName,
            bookedRoom = requestedRoom,
            bookedEquipment = requestedEquipment.ifEmpty {
                mutableSetOf()
            }
        )

        bookingRepository.save(booking)
        return BookingResponse.mapFrom(booking)
    }

    /**
     * Updates existing booking in repository if all conditions are met:
     * - provided booking_id exists
     * - booking time falls within allowed range (start, end times, time span)
     * - requested room is available
     * - requested equipment is available
     *
     * @param bookingId booking to update
     * @param bookingRequest new booking details
     * @return updated booking details
     * @throws IllegalStateException requested times are invalid
     * @throws IllegalArgumentException requested resource(s) unavailable
     * @throws ResourceNotFoundException resource does not exist
     */
    @Transactional
    fun updateBooking(bookingId: Long, bookingRequest: BookingRequest): BookingResponse {
        checkRequestContainsValidTimesOrThrow(bookingRequest)

        val bookingToUpdate = bookingRepository.findById(bookingId)
            .orElseThrow { EntityNotFoundException("No such booking exists") }
        val requestedRoom = meetingRoomRepository.findById(bookingRequest.meetingRoomId)
            .orElseThrow { ResourceNotFoundException("No such room exists") }

        val existingBookings =
            bookingRepository.findAllOverlappingByTime(bookingRequest.startTime, bookingRequest.endTime)
                .filterNot { existingBooking -> existingBooking.getId()!! == bookingToUpdate.getId() }

        checkResourcesAvailabilityOrThrow(existingBookings, requestedRoom, bookingRequest)

        val requestedEquipment = equipmentRepository.findAllById(bookingRequest.equipmentIds)
            .toMutableSet()

        bookingToUpdate.startTime = bookingRequest.startTime
        bookingToUpdate.endTime = bookingRequest.endTime
        bookingToUpdate.firstName = bookingRequest.firstName
        bookingToUpdate.lastName = bookingRequest.lastName
        bookingToUpdate.bookedRoom = requestedRoom
        bookingToUpdate.bookedEquipment = requestedEquipment.ifEmpty {
            mutableSetOf()
        }

        bookingRepository.save(bookingToUpdate)
        return BookingResponse.mapFrom(bookingToUpdate)
    }

    /**
     * Removes booking with provided booking_id from repository
     * @param bookingId booking record to remove
     */
    @Transactional
    fun cancelBooking(bookingId: Long) {
        bookingRepository.deleteById(bookingId)
    }

    private fun checkResourcesAvailabilityOrThrow(
        existingBookings: List<Booking>,
        requestedRoom: MeetingRoom,
        bookingRequest: BookingRequest
    ) {
        val roomAvailable = isRoomAvailable(existingBookings, requestedRoom)
        val equipmentAvailable = isEquipmentAvailable(existingBookings, bookingRequest.equipmentIds)
        require(roomAvailable) { "Room not available" }
        require(equipmentAvailable) { "Equipment not available" }
    }

    private fun checkRequestContainsValidTimesOrThrow(bookingRequest: BookingRequest) {
        val bookingStartTime = bookingRequest.startTime.toLocalTime()
        val bookingEndTime = bookingRequest.endTime.toLocalTime()

        val startBeforeEnd = bookingRequest.startTime < bookingRequest.endTime
        val startInAllowedRange = bookingStartTime >= properties.allowedStartTime
                && bookingStartTime <= properties.allowedEndTime
        val endInAllowedRange = bookingEndTime >= properties.allowedStartTime
                && bookingEndTime <= properties.allowedEndTime
        val durationInRange = Duration.between(bookingRequest.startTime, bookingRequest.endTime).toHours() <=
                Duration.between(properties.allowedStartTime, properties.allowedEndTime).toHours()

        check(startBeforeEnd) { "Start time must precede end time" }
        check(startInAllowedRange) { "Start time not in allowed range" }
        check(endInAllowedRange) { "End time not in allowed range" }
        check(durationInRange) { "Booking time exceeds allowed range" }
    }

    private fun isRoomAvailable(
        existingBookings: List<Booking>,
        requestedRoom: MeetingRoom
    ) = existingBookings
        .map { existingBooking -> existingBooking.bookedRoom.getId()!! }
        .contains(requestedRoom.getId()!!)
        .not()

    private fun isEquipmentAvailable(
        existingBookings: List<Booking>,
        equipmentIds: List<Long>
    ) = existingBookings
        .map { existingBooking -> existingBooking.bookedEquipment }
        .distinct()
        .flatten()
        .map { bookedEquipment -> bookedEquipment.getId()!! }
        .any { bookedEquipmentId -> bookedEquipmentId in equipmentIds }
        .not()

}
