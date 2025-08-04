package com.cargopro.repository;

import com.cargopro.entity.Booking;
import com.cargopro.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    
    @Query("SELECT b FROM Booking b WHERE " +
           "(:loadId IS NULL OR b.load.id = :loadId) AND " +
           "(:transporterId IS NULL OR b.transporterId = :transporterId) AND " +
           "(:status IS NULL OR b.status = :status)")
    Page<Booking> findBookingsWithFilters(@Param("loadId") UUID loadId,
                                         @Param("transporterId") String transporterId,
                                         @Param("status") BookingStatus status,
                                         Pageable pageable);
    
    List<Booking> findByLoadId(UUID loadId);
    
    List<Booking> findByLoadIdAndStatus(UUID loadId, BookingStatus status);
    
    Page<Booking> findByTransporterId(String transporterId, Pageable pageable);
    
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
    
    boolean existsByLoadIdAndTransporterId(UUID loadId, String transporterId);
}