package com.cargopro.service;

import com.cargopro.dto.FacilityDto;
import com.cargopro.dto.LoadDto;
import com.cargopro.entity.Facility;
import com.cargopro.entity.Load;
import com.cargopro.enums.LoadStatus;
import com.cargopro.exception.ResourceNotFoundException;
import com.cargopro.mapper.LoadMapper;
import com.cargopro.repository.LoadRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoadServiceTest {
    
    @Mock
    private LoadRepository loadRepository;
    
    @Mock
    private LoadMapper loadMapper;
    
    @InjectMocks
    private LoadService loadService;
    
    private Load testLoad;
    private LoadDto testLoadDto;
    private UUID testLoadId;
    
    @BeforeEach
    void setUp() {
        testLoadId = UUID.randomUUID();
        
        Facility facility = new Facility("Mumbai", "Delhi", 
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
        
        testLoad = new Load("SHIPPER001", facility, "Electronics", "Container", 2, 15.5, "Test comment");
        testLoad.setId(testLoadId);
        testLoad.setStatus(LoadStatus.POSTED);
        
        FacilityDto facilityDto = new FacilityDto("Mumbai", "Delhi", 
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
        
        testLoadDto = new LoadDto(testLoadId, "SHIPPER001", facilityDto, "Electronics", 
            "Container", 2, 15.5, "Test comment", LocalDateTime.now(), LoadStatus.POSTED);
    }
    
    @Test
    void createLoad_ShouldReturnLoadDto_WhenValidInput() {
        // Given
        when(loadMapper.toEntity(testLoadDto)).thenReturn(testLoad);
        when(loadRepository.save(any(Load.class))).thenReturn(testLoad);
        when(loadMapper.toDto(testLoad)).thenReturn(testLoadDto);
        
        // When
        LoadDto result = loadService.createLoad(testLoadDto);
        
        // Then
        assertNotNull(result);
        assertEquals(testLoadDto.getShipperId(), result.getShipperId());
        assertEquals(LoadStatus.POSTED, result.getStatus());
        verify(loadRepository).save(any(Load.class));
    }
    
    @Test
    void getLoadById_ShouldReturnLoadDto_WhenLoadExists() {
        // Given
        when(loadRepository.findById(testLoadId)).thenReturn(Optional.of(testLoad));
        when(loadMapper.toDto(testLoad)).thenReturn(testLoadDto);
        
        // When
        LoadDto result = loadService.getLoadById(testLoadId);
        
        // Then
        assertNotNull(result);
        assertEquals(testLoadId, result.getId());
        verify(loadRepository).findById(testLoadId);
    }
    
    @Test
    void getLoadById_ShouldThrowException_WhenLoadNotFound() {
        // Given
        when(loadRepository.findById(testLoadId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> loadService.getLoadById(testLoadId));
        verify(loadRepository).findById(testLoadId);
    }
    
    @Test
    void getLoads_ShouldReturnPageOfLoads_WhenFiltersApplied() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Load> loadPage = new PageImpl<>(Arrays.asList(testLoad));
        Page<LoadDto> expectedPage = new PageImpl<>(Arrays.asList(testLoadDto));
        
        when(loadRepository.findLoadsWithFilters("SHIPPER001", "Container", LoadStatus.POSTED, pageable))
            .thenReturn(loadPage);
        when(loadMapper.toDto(testLoad)).thenReturn(testLoadDto);
        
        // When
        Page<LoadDto> result = loadService.getLoads("SHIPPER001", "Container", LoadStatus.POSTED, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testLoadDto.getShipperId(), result.getContent().get(0).getShipperId());
    }
    
    @Test
    void updateLoad_ShouldReturnUpdatedLoadDto_WhenLoadExists() {
        // Given
        LoadDto updateDto = new LoadDto();
        updateDto.setShipperId("UPDATED_SHIPPER");
        
        when(loadRepository.findById(testLoadId)).thenReturn(Optional.of(testLoad));
        when(loadRepository.save(testLoad)).thenReturn(testLoad);
        when(loadMapper.toDto(testLoad)).thenReturn(testLoadDto);
        
        // When
        LoadDto result = loadService.updateLoad(testLoadId, updateDto);
        
        // Then
        assertNotNull(result);
        verify(loadMapper).updateEntityFromDto(updateDto, testLoad);
        verify(loadRepository).save(testLoad);
    }
    
    @Test
    void deleteLoad_ShouldDeleteLoad_WhenLoadExists() {
        // Given
        when(loadRepository.findById(testLoadId)).thenReturn(Optional.of(testLoad));
        
        // When
        loadService.deleteLoad(testLoadId);
        
        // Then
        verify(loadRepository).delete(testLoad);
    }
    
    @Test
    void updateLoadStatus_ShouldUpdateStatus_WhenLoadExists() {
        // Given
        when(loadRepository.findById(testLoadId)).thenReturn(Optional.of(testLoad));
        when(loadRepository.save(testLoad)).thenReturn(testLoad);
        
        // When
        loadService.updateLoadStatus(testLoadId, LoadStatus.BOOKED);
        
        // Then
        assertEquals(LoadStatus.BOOKED, testLoad.getStatus());
        verify(loadRepository).save(testLoad);
    }
}