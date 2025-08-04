package com.cargopro.repository;

import com.cargopro.entity.Load;
import com.cargopro.enums.LoadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoadRepository extends JpaRepository<Load, UUID> {
    
    @Query("SELECT l FROM Load l WHERE " +
           "(:shipperId IS NULL OR l.shipperId = :shipperId) AND " +
           "(:truckType IS NULL OR l.truckType = :truckType) AND " +
           "(:status IS NULL OR l.status = :status)")
    Page<Load> findLoadsWithFilters(@Param("shipperId") String shipperId,
                                   @Param("truckType") String truckType,
                                   @Param("status") LoadStatus status,
                                   Pageable pageable);
    
    Page<Load> findByShipperId(String shipperId, Pageable pageable);
    
    Page<Load> findByTruckType(String truckType, Pageable pageable);
    
    Page<Load> findByStatus(LoadStatus status, Pageable pageable);
}