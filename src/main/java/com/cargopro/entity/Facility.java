package com.cargopro.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Embeddable
public class Facility {
    
    @NotBlank(message = "Loading point is required")
    @Column(name = "loading_point", nullable = false)
    private String loadingPoint;
    
    @NotBlank(message = "Unloading point is required")
    @Column(name = "unloading_point", nullable = false)
    private String unloadingPoint;
    
    @NotNull(message = "Loading date is required")
    @Column(name = "loading_date", nullable = false)
    private LocalDateTime loadingDate;
    
    @NotNull(message = "Unloading date is required")
    @Column(name = "unloading_date", nullable = false)
    private LocalDateTime unloadingDate;
    
    // Constructors
    public Facility() {}
    
    public Facility(String loadingPoint, String unloadingPoint, 
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