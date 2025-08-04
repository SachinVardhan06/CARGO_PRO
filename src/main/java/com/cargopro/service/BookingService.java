package com.cargopro.service;

import com.cargopro.dto.BookingDto;
import com.cargopro.entity.Booking;
import com.cargopro.entity.Load;
import com.cargopro.enums.BookingStatus;
import com.cargopro.enums.LoadStatus;
import com.cargopro.exception.BusinessException;
import com.cargopro.exception.ResourceNotFoundException;
import com.cargopro.mapper.BookingMapper;
import com.cargopro.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final LoadService loadService;
    
    @Autowired
    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper, LoadService loadService) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.loadService = loadService;
    }
    
    public BookingDto createBooking(BookingDto bookingDto) {
        Load load = loadService.getLoadEntityById(bookingDto.getLoadId());
        
        // Business rule: Cannot book a cancelled load
        if (load.getStatus() == LoadStatus.CANCELLED) {
            throw new BusinessException("Cannot create booking for a cancelled load");
        }
        
        // Check if transporter already has a booking for this load
        if (bookingRepository.existsByLoadIdAndTransporterId(bookingDto.getLoadId(), bookingDto.getTransporterId())) {
            throw new BusinessException("Transporter already has a booking for this load");
        }
        
        Booking booking = bookingMapper.toEntity(bookingDto);
        booking.setLoad(load);
        booking.setStatus(BookingStatus.PENDING);
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Update load status to BOOKED when first booking is created
        if (load.getStatus() == LoadStatus.POSTED) {
            loadService.updateLoadStatus(load.getId(), LoadStatus.BOOKED);
        }
        
        return bookingMapper.toDto(savedBooking);
    }
    
    @Transactional(readOnly = true)
    public Page<BookingDto> getBookings(UUID loadId, String transporterId, BookingStatus status, Pageable pageable) {
        Page<Booking> bookings = bookingRepository.findBookingsWithFilters(loadId, transporterId, status, pageable);
        return bookings.map(bookingMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public BookingDto getBookingById(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        return bookingMapper.toDto(booking);
    }
    
    public BookingDto updateBooking(UUID bookingId, BookingDto bookingDto) {
        Booking existingBooking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        
        BookingStatus oldStatus = existingBooking.getStatus();
        bookingMapper.updateEntityFromDto(bookingDto, existingBooking);
        
        Booking updatedBooking = bookingRepository.save(existingBooking);
        
        // Handle status transitions
        handleBookingStatusChange(updatedBooking, oldStatus);
        
        return bookingMapper.toDto(updatedBooking);
    }
    
    public void deleteBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        
        UUID loadId = booking.getLoad().getId();
        bookingRepository.delete(booking);
        
        // Check if this was the last booking for the load
        List<Booking> remainingBookings = bookingRepository.findByLoadId(loadId);
        if (remainingBookings.isEmpty()) {
            loadService.updateLoadStatus(loadId, LoadStatus.CANCELLED);
        } else {
            // Check if all remaining bookings are rejected
            boolean allRejected = remainingBookings.stream()
                .allMatch(b -> b.getStatus() == BookingStatus.REJECTED);
            if (allRejected) {
                loadService.updateLoadStatus(loadId, LoadStatus.POSTED);
            }
        }
    }
    
    private void handleBookingStatusChange(Booking booking, BookingStatus oldStatus) {
        if (booking.getStatus() == BookingStatus.ACCEPTED && oldStatus != BookingStatus.ACCEPTED) {
            // When a booking is accepted, reject all other bookings for the same load
            List<Booking> otherBookings = bookingRepository.findByLoadId(booking.getLoad().getId());
            for (Booking otherBooking : otherBookings) {
                if (!otherBooking.getId().equals(booking.getId()) && 
                    otherBooking.getStatus() == BookingStatus.PENDING) {
                    otherBooking.setStatus(BookingStatus.REJECTED);
                    bookingRepository.save(otherBooking);
                }
            }
        }
        
        // Check if all bookings are rejected, then revert load status to POSTED
        if (booking.getStatus() == BookingStatus.REJECTED) {
            List<Booking> allBookings = bookingRepository.findByLoadId(booking.getLoad().getId());
            boolean allRejected = allBookings.stream()
                .allMatch(b -> b.getStatus() == BookingStatus.REJECTED);
            if (allRejected) {
                loadService.updateLoadStatus(booking.getLoad().getId(), LoadStatus.POSTED);
            }
        }
    }
}