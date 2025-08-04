package com.cargopro.service;

import com.cargopro.dto.LoadDto;
import com.cargopro.entity.Load;
import com.cargopro.enums.LoadStatus;
import com.cargopro.exception.ResourceNotFoundException;
import com.cargopro.mapper.LoadMapper;
import com.cargopro.repository.LoadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class LoadService {
    
    private final LoadRepository loadRepository;
    private final LoadMapper loadMapper;
    
    @Autowired
    public LoadService(LoadRepository loadRepository, LoadMapper loadMapper) {
        this.loadRepository = loadRepository;
        this.loadMapper = loadMapper;
    }
    
    public LoadDto createLoad(LoadDto loadDto) {
        Load load = loadMapper.toEntity(loadDto);
        load.setStatus(LoadStatus.POSTED); // Ensure status is POSTED for new loads
        Load savedLoad = loadRepository.save(load);
        return loadMapper.toDto(savedLoad);
    }
    
    @Transactional(readOnly = true)
    public Page<LoadDto> getLoads(String shipperId, String truckType, LoadStatus status, Pageable pageable) {
        Page<Load> loads = loadRepository.findLoadsWithFilters(shipperId, truckType, status, pageable);
        return loads.map(loadMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public LoadDto getLoadById(UUID loadId) {
        Load load = loadRepository.findById(loadId)
            .orElseThrow(() -> new ResourceNotFoundException("Load not found with id: " + loadId));
        return loadMapper.toDto(load);
    }
    
    public LoadDto updateLoad(UUID loadId, LoadDto loadDto) {
        Load existingLoad = loadRepository.findById(loadId)
            .orElseThrow(() -> new ResourceNotFoundException("Load not found with id: " + loadId));
        
        loadMapper.updateEntityFromDto(loadDto, existingLoad);
        Load updatedLoad = loadRepository.save(existingLoad);
        return loadMapper.toDto(updatedLoad);
    }
    
    public void deleteLoad(UUID loadId) {
        Load load = loadRepository.findById(loadId)
            .orElseThrow(() -> new ResourceNotFoundException("Load not found with id: " + loadId));
        
        loadRepository.delete(load);
    }
    
    public void updateLoadStatus(UUID loadId, LoadStatus status) {
        Load load = loadRepository.findById(loadId)
            .orElseThrow(() -> new ResourceNotFoundException("Load not found with id: " + loadId));
        
        load.setStatus(status);
        loadRepository.save(load);
    }
    
    @Transactional(readOnly = true)
    public Load getLoadEntityById(UUID loadId) {
        return loadRepository.findById(loadId)
            .orElseThrow(() -> new ResourceNotFoundException("Load not found with id: " + loadId));
    }
}