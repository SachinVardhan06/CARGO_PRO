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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    
    @Mock
    private BookingRepository bookingRepository;
    
    @Mock
    private BookingMapper bookingMapper;
    
    @Mock
    private LoadService loadService;
    
    @InjectMocks
    private BookingService bookingService;
    
    private Booking testBooking;
    private BookingDto testBookingDto;
    private Load testLoad;
    private UUID testBookingId;
    private UUID testLoadId;
    
    @BeforeEach
    void setUp() {
        testBookingId = UUID.randomUUID();
        testLoadId = UUID.randomUUID();
        
        testLoad = new Load();
        testLoad.setId(testLoadId);
        testLoad.setStatus(LoadStatus.POSTED);
        
        testBooking = new Booking(testLoad, "TRANS001", 25000.0, "Test booking");
        testBooking.setId(testBookingId);
        testBooking.setStatus(BookingStatus.PENDING);
        
        testBookingDto = new BookingDto(testBookingId, testLoadId, "TRANS001", 25000.0, 
            "Test booking", BookingStatus.PENDING, LocalDateTime.now());
    }
    
    @Test
    void createBooking_ShouldReturnBookingDto_WhenValidInput() {
        // Given
        when(loadService.getLoadEntityById(testLoadId)).thenReturn(testLoad);
        when(bookingRepository.existsByLoadIdAndTransporterId(testLoadId, "TRANS001")).thenReturn(false);
        when(bookingMapper.toEntity(testBookingDto)).thenReturn(testBooking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(bookingMapper.toDto(testBooking)).thenReturn(testBookingDto);
        
        // When
        BookingDto result = bookingService.createBooking(testBookingDto);
        
        // Then
        assertNotNull(result);
        assertEquals(testBookingDto.getTransporterId(), result.getTransporterId());
        assertEquals(BookingStatus.PENDING, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
        verify(loadService).updateLoadStatus(testLoadId, LoadStatus.BOOKED);
    }
    
    @Test
    void createBooking_ShouldThrowException_WhenLoadIsCancelled() {
        // Given
        testLoad.setStatus(LoadStatus.CANCELLED);
        when(loadService.getLoadEntityById(testLoadId)).thenReturn(testLoad);
        
        // When & Then
        assertThrows(BusinessException.class, () -> bookingService.createBooking(testBookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
    
    @Test
    void createBooking_ShouldThrowException_WhenTransporterAlreadyHasBooking() {
        // Given
        when(loadService.getLoadEntityById(testLoadId)).thenReturn(testLoad);
        when(bookingRepository.existsByLoadIdAndTransporterId(testLoadId, "TRANS001")).thenReturn(true);
        
        // When & Then
        assertThrows(BusinessException.class, () -> bookingService.createBooking(testBookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
    
    @Test
    void getBookingById_ShouldReturnBookingDto_WhenBookingExists() {
        // Given
        when(bookingRepository.findById(testBookingId)).thenReturn(Optional.of(testBooking));
        when(bookingMapper.toDto(testBooking)).thenReturn(testBookingDto);
        
        // When
        BookingDto result = bookingService.getBookingById(testBookingId);
        
        // Then
        assertNotNull(result);
        assertEquals(testBookingId, result.getId());
        verify(bookingRepository).findById(testBookingId);
    }
    
    @Test
    void getBookingById_ShouldThrowException_WhenBookingNotFound() {
        // Given
        when(bookingRepository.findById(testBookingId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(testBookingId));
        verify(bookingRepository).findById(testBookingId);
    }
    
    @Test
    void getBookings_ShouldReturnPageOfBookings_WhenFiltersApplied() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> bookingPage = new PageImpl<>(Arrays.asList(testBooking));
        
        when(bookingRepository.findBookingsWithFilters(testLoadId, "TRANS001", BookingStatus.PENDING, pageable))
            .thenReturn(bookingPage);
        when(bookingMapper.toDto(testBooking)).thenReturn(testBookingDto);
        
        // When
        Page<BookingDto> result = bookingService.getBookings(testLoadId, "TRANS001", BookingStatus.PENDING, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testBookingDto.getTransporterId(), result.getContent().get(0).getTransporterId());
    }
    
    @Test
    void updateBooking_ShouldReturnUpdatedBookingDto_WhenBookingExists() {
        // Given
        BookingDto updateDto = new BookingDto();
        updateDto.setStatus(BookingStatus.ACCEPTED);
        
        when(bookingRepository.findById(testBookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(testBooking)).thenReturn(testBooking);
        when(bookingMapper.toDto(testBooking)).thenReturn(testBookingDto);
        when(bookingRepository.findByLoadId(testLoadId)).thenReturn(Arrays.asList(testBooking));
        
        // When
        BookingDto result = bookingService.updateBooking(testBookingId, updateDto);
        
        // Then
        assertNotNull(result);
        verify(bookingMapper).updateEntityFromDto(updateDto, testBooking);
        verify(bookingRepository).save(testBooking);
    }
    
    @Test
    void deleteBooking_ShouldDeleteBookingAndUpdateLoadStatus_WhenLastBooking() {
        // Given
        when(bookingRepository.findById(testBookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.findByLoadId(testLoadId)).thenReturn(Collections.emptyList());
        
        // When
        bookingService.deleteBooking(testBookingId);
        
        // Then
        verify(bookingRepository).delete(testBooking);
        verify(loadService).updateLoadStatus(testLoadId, LoadStatus.CANCELLED);
    }
    
    @Test
    void deleteBooking_ShouldDeleteBookingAndRevertLoadStatus_WhenAllRemainingBookingsRejected() {
        // Given
        Booking rejectedBooking = new Booking();
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        
        when(bookingRepository.findById(testBookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.findByLoadId(testLoadId)).thenReturn(Arrays.asList(rejectedBooking));
        
        // When
        bookingService.deleteBooking(testBookingId);
        
        // Then
        verify(bookingRepository).delete(testBooking);
        verify(loadService).updateLoadStatus(testLoadId, LoadStatus.POSTED);
    }
}