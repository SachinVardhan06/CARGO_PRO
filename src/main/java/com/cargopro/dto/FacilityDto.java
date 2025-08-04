package com.cargopro.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Facility Data Transfer Object")
public class FacilityDto {
    
    @NotBlank(message = "Loading point is required")
    @Schema(description = "Loading point", example = "Mumbai Port")
    private String loadingPoint;
    
    @NotBlank(message = "Unloading point is required")
    @Schema(description = "Unloading point", example = "Delhi Warehouse")
    private String unloadingPoint;
    
    @NotNull(message = "Loading date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Schema(description = "Loading date", example = "2024-01-20T08:00:00.000Z")
    private LocalDateTime loadingDate;
    
    @NotNull(message = "Unloading date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Schema(description = "Unloading date", example = "2024-01-22T18:00:00.000Z")
    private LocalDateTime unloadingDate;
    
    // Constructors
    public FacilityDto() {}
    
    public FacilityDto(String loadingPoint, String unloadingPoint, 
                      LocalDateTime loadingDate, LocalDateTime unloadingDate) {
        this.loadingPoint = loadingPoint;
        this.unloadingPoint = unloadingPoint;
        this.loadingDate = loadingDate;
        this.unloadingDate = unloadingDate;
    }
    
    // Getters and Setters
    public String getLoadingPoint() { return loadingPoint; }
    public void setLoadingPoint(String loadingPoint) { this.loadingPoint = loadingPoint; }
    
    public String getUnloadingPoint() { return unloadingPoint; }
    public void setUnloadingPoint(String unloadingPoint) { this.unloadingPoint = unloadingPoint; }
    
    public LocalDateTime getLoadingDate() { return loadingDate; }
    public void setLoadingDate(LocalDateTime loadingDate) { this.loadingDate = loadingDate; }
    
    public LocalDateTime getUnloadingDate() { return unloadingDate; }
    public void setUnloadingDate(LocalDateTime unloadingDate) { this.unloadingDate = unloadingDate; }
}