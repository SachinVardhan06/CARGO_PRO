package com.cargopro.mapper;

import com.cargopro.dto.BookingDto;
import com.cargopro.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    
    public BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        
        return new BookingDto(
            booking.getId(),
            booking.getLoad().getId(),
            booking.getTransporterId(),
            booking.getProposedRate(),
            booking.getComment(),
            booking.getStatus(),
            booking.getRequestedAt()
        );
    }
    
    public Booking toEntity(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }
        
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setTransporterId(bookingDto.getTransporterId());
        booking.setProposedRate(bookingDto.getProposedRate());
        booking.setComment(bookingDto.getComment());
        booking.setStatus(bookingDto.getStatus());
        booking.setRequestedAt(bookingDto.getRequestedAt());
        
        return booking;
    }
    
    public void updateEntityFromDto(BookingDto bookingDto, Booking booking) {
        if (bookingDto == null || booking == null) {
            return;
        }
        
        booking.setTransporterId(bookingDto.getTransporterId());
        booking.setProposedRate(bookingDto.getProposedRate());
        booking.setComment(bookingDto.getComment());
        booking.setStatus(bookingDto.getStatus());
    }
}