package com.sample.booking.controller

import com.sample.booking.domain.rest.BookingRequest
import com.sample.booking.domain.rest.BookingResponse
import com.sample.booking.service.BookingService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/bookings")
class BookingController(
    val bookingService: BookingService
) {

    @GetMapping
    fun getBookings(): List<BookingResponse> {
        return bookingService.getBookings()
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createBooking(@RequestBody bookingRequest: BookingRequest): BookingResponse {
        return bookingService.createBooking(bookingRequest)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/{bookingId}")
    fun updateBooking(
        @PathVariable bookingId: Long,
        @RequestBody bookingRequest: BookingRequest
    ): BookingResponse {
        return bookingService.updateBooking(bookingId, bookingRequest)
    }

    @DeleteMapping("/{bookingId}")
    fun deleteBooking(@PathVariable bookingId: Long) {
        bookingService.cancelBooking(bookingId)
    }
}
