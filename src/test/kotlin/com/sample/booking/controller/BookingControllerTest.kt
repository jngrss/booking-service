package com.sample.booking.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.ninjasquad.springmockk.MockkBean
import com.sample.booking.TestData.getSampleBookingRequest
import com.sample.booking.TestData.getSampleBookingResponse
import com.sample.booking.domain.exception.ResourceNotFoundException
import com.sample.booking.service.BookingService
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(BookingController::class)
@ActiveProfiles("test")
class BookingControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var bookingService: BookingService

    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .findAndRegisterModules()
    }

    @Test
    fun `Get existing bookings`() {
        val response = getSampleBookingResponse()
        every { bookingService.getBookings() } returns listOf(response)

        mockMvc.perform(
            get("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id", equalTo(response.id), Long::class.java))

        verify { bookingService.getBookings() }
        confirmVerified(bookingService)
    }

    @Test
    fun `Create new booking - success`() {
        val request = getSampleBookingRequest(10, emptyList())
        every { bookingService.createBooking(request) } returns getSampleBookingResponse()

        mockMvc.perform(post("/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id", equalTo(1), Long::class.java))

        verify { bookingService.createBooking(request) }
        confirmVerified(bookingService)
    }

    @Test
    fun `Create new booking - invalid meeting room requested`() {
        val request = getSampleBookingRequest(10, emptyList())
        every { bookingService.createBooking(request) } throws ResourceNotFoundException("No such room exists")

        mockMvc.perform(post("/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest)

        verify { bookingService.createBooking(request) }
        confirmVerified(bookingService)
    }

    @Test
    fun `Create new booking - invalid equipment requested`() {
        val request = getSampleBookingRequest(10, emptyList())
        every { bookingService.createBooking(request) } throws IllegalArgumentException("Equipment not available")

        mockMvc.perform(post("/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest)

        verify { bookingService.createBooking(request) }
        confirmVerified(bookingService)
    }

    @Test
    fun `Create new booking - invalid time requested`() {
        val request = getSampleBookingRequest(10, emptyList())
        every { bookingService.createBooking(request) } throws IllegalStateException("Booking time exceeds allowed range")

        mockMvc.perform(post("/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest)

        verify { bookingService.createBooking(request) }
        confirmVerified(bookingService)
    }

    @Test
    fun `Update booking - success`() {
        val request = getSampleBookingRequest(10, emptyList())
        every { bookingService.updateBooking(1, request) } returns getSampleBookingResponse()

        mockMvc.perform(put("/bookings/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id", equalTo(1), Long::class.java))

        verify { bookingService.updateBooking(1, request) }
        confirmVerified(bookingService)
    }

    @Test
    fun `Update booking - invalid booking id`() {
        val request = getSampleBookingRequest(10, emptyList())
        every { bookingService.updateBooking(1, request) } throws EntityNotFoundException("No such booking exists")

        mockMvc.perform(put("/bookings/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound)

        verify { bookingService.updateBooking(1, request) }
        confirmVerified(bookingService)
    }

    @Test
    fun `Delete booking`() {
        every { bookingService.cancelBooking(1) } just Runs

        mockMvc.perform(delete("/bookings/1"))
            .andExpect(status().isOk)

        verify { bookingService.cancelBooking(1) }
        confirmVerified(bookingService)
    }
}