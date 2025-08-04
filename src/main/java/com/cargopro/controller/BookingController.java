package com.cargopro.controller;

import com.cargopro.dto.BookingDto;
import com.cargopro.enums.BookingStatus;
import com.cargopro.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/booking")
@Tag(name = "Booking Management", description = "APIs for managing bookings")
public class BookingController {
    
    private final BookingService bookingService;
    
    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    @PostMapping
    @Operation(summary = "Create a new booking", description = "Creates a new booking for a load")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Booking created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
        @ApiResponse(responseCode = "404", description = "Load not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingDto bookingDto) {
        BookingDto createdBooking = bookingService.createBooking(bookingDto);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get bookings with filters", description = "Retrieves bookings with optional filtering and pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<BookingDto>> getBookings(
            @Parameter(description = "Filter by load ID") @RequestParam(required = false) UUID loadId,
            @Parameter(description = "Filter by transporter ID") @RequestParam(required = false) String transporterId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) BookingStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "requestedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BookingDto> bookings = bookingService.getBookings(loadId, transporterId, status, pageable);
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking by ID", description = "Retrieves a specific booking by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking found"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BookingDto> getBookingById(@PathVariable UUID bookingId) {
        BookingDto booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }
    
    @PutMapping("/{bookingId}")
    @Operation(summary = "Update booking", description = "Updates an existing booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BookingDto> updateBooking(@PathVariable UUID bookingId, @Valid @RequestBody BookingDto bookingDto) {
        BookingDto updatedBooking = bookingService.updateBooking(bookingId, bookingDto);
        return ResponseEntity.ok(updatedBooking);
    }
    
    @DeleteMapping("/{bookingId}")
    @Operation(summary = "Delete booking", description = "Deletes a booking and updates load status if necessary")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteBooking(@PathVariable UUID bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}