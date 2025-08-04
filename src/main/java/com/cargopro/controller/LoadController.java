package com.cargopro.controller;

import com.cargopro.dto.LoadDto;
import com.cargopro.enums.LoadStatus;
import com.cargopro.service.LoadService;
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
@RequestMapping("/load")
@Tag(name = "Load Management", description = "APIs for managing loads")
public class LoadController {
    
    private final LoadService loadService;
    
    @Autowired
    public LoadController(LoadService loadService) {
        this.loadService = loadService;
    }
    
    @PostMapping
    @Operation(summary = "Create a new load", description = "Creates a new load with POSTED status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Load created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoadDto> createLoad(@Valid @RequestBody LoadDto loadDto) {
        LoadDto createdLoad = loadService.createLoad(loadDto);
        return new ResponseEntity<>(createdLoad, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get loads with filters", description = "Retrieves loads with optional filtering and pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loads retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<LoadDto>> getLoads(
            @Parameter(description = "Filter by shipper ID") @RequestParam(required = false) String shipperId,
            @Parameter(description = "Filter by truck type") @RequestParam(required = false) String truckType,
            @Parameter(description = "Filter by status") @RequestParam(required = false) LoadStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "datePosted") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<LoadDto> loads = loadService.getLoads(shipperId, truckType, status, pageable);
        return ResponseEntity.ok(loads);
    }
    
    @GetMapping("/{loadId}")
    @Operation(summary = "Get load by ID", description = "Retrieves a specific load by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Load found"),
        @ApiResponse(responseCode = "404", description = "Load not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoadDto> getLoadById(@PathVariable UUID loadId) {
        LoadDto load = loadService.getLoadById(loadId);
        return ResponseEntity.ok(load);
    }
    
    @PutMapping("/{loadId}")
    @Operation(summary = "Update load", description = "Updates an existing load")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Load updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Load not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoadDto> updateLoad(@PathVariable UUID loadId, @Valid @RequestBody LoadDto loadDto) {
        LoadDto updatedLoad = loadService.updateLoad(loadId, loadDto);
        return ResponseEntity.ok(updatedLoad);
    }
    
    @DeleteMapping("/{loadId}")
    @Operation(summary = "Delete load", description = "Deletes a load and all associated bookings")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Load deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Load not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteLoad(@PathVariable UUID loadId) {
        loadService.deleteLoad(loadId);
        return ResponseEntity.noContent().build();
    }
}