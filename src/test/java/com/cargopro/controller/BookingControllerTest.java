package com.cargopro.controller;

import com.cargopro.dto.BookingDto;
import com.cargopro.enums.BookingStatus;
import com.cargopro.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookingService bookingService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private BookingDto testBookingDto;
    private UUID testBookingId;
    private UUID testLoadId;
    
    @BeforeEach
    void setUp() {
        testBookingId = UUID.randomUUID();
        testLoadId = UUID.randomUUID();
        
        testBookingDto = new BookingDto(testBookingId, testLoadId, "TRANS001", 25000.0, 
            "Test booking", BookingStatus.PENDING, LocalDateTime.now());
    }
    
    @Test
    void createBooking_ShouldReturnCreatedBooking_WhenValidInput() throws Exception {
        // Given
        when(bookingService.createBooking(any(BookingDto.class))).thenReturn(testBookingDto);
        
        // When & Then
        mockMvc.perform(post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testBookingId.toString()))
                .andExpect(jsonPath("$.transporterId").value("TRANS001"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
    
    @Test
    void createBooking_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given
        BookingDto invalidBooking = new BookingDto();
        invalidBooking.setTransporterId(""); // Invalid empty transporter ID
        
        // When & Then
        mockMvc.perform(post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBooking)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getBookings_ShouldReturnPageOfBookings_WhenValidRequest() throws Exception {
        // Given
        Page<BookingDto> bookingPage = new PageImpl<>(Arrays.asList(testBookingDto));
        when(bookingService.getBookings(eq(testLoadId), eq("TRANS001"), eq(BookingStatus.PENDING), any()))
            .thenReturn(bookingPage);
        
        // When & Then
        mockMvc.perform(get("/booking")
                .param("loadId", testLoadId.toString())
                .param("transporterId", "TRANS001")
                .param("status", "PENDING")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].transporterId").value("TRANS001"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    void getBookingById_ShouldReturnBooking_WhenBookingExists() throws Exception {
        // Given
        when(bookingService.getBookingById(testBookingId)).thenReturn(testBookingDto);
        
        // When & Then
        mockMvc.perform(get("/booking/{bookingId}", testBookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBookingId.toString()))
                .andExpect(jsonPath("$.transporterId").value("TRANS001"));
    }
    
    @Test
    void updateBooking_ShouldReturnUpdatedBooking_WhenValidInput() throws Exception {
        // Given
        BookingDto updatedBooking = new BookingDto();
        updatedBooking.setLoadId(testLoadId);
        updatedBooking.setTransporterId("UPDATED_TRANS");
        updatedBooking.setProposedRate(30000.0);
        updatedBooking.setStatus(BookingStatus.ACCEPTED);
        
        when(bookingService.updateBooking(eq(testBookingId), any(BookingDto.class))).thenReturn(updatedBooking);
        
        // When & Then
        mockMvc.perform(put("/booking/{bookingId}", testBookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transporterId").value("UPDATED_TRANS"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }
    
    @Test
    void deleteBooking_ShouldReturnNoContent_WhenBookingExists() throws Exception {
        // When & Then
        mockMvc.perform(delete("/booking/{bookingId}", testBookingId))
                .andExpect(status().isNoContent());
    }
}