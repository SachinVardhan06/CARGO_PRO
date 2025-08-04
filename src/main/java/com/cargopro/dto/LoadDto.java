package com.cargopro.dto;

import com.cargopro.enums.LoadStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Load Data Transfer Object")
public class LoadDto {
    
    @Schema(description = "Load ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @NotBlank(message = "Shipper ID is required")
    @Schema(description = "Shipper ID", example = "SHIPPER001")
    private String shipperId;
    
    @Valid
    @NotNull(message = "Facility information is required")
    @Schema(description = "Facility information")
    private FacilityDto facility;
    
    @NotBlank(message = "Product type is required")
    @Schema(description = "Product type", example = "Electronics")
    private String productType;
    
    @NotBlank(message = "Truck type is required")
    @Schema(description = "Truck type", example = "Container")
    private String truckType;
    
    @NotNull(message = "Number of trucks is required")
    @Positive(message = "Number of trucks must be positive")
    @Schema(description = "Number of trucks", example = "2")
    private Integer noOfTrucks;
    
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @Schema(description = "Weight in tons", example = "15.5")
    private Double weight;
    
    @Schema(description = "Additional comments", example = "Handle with care")
    private String comment;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Schema(description = "Date when load was posted", example = "2024-01-15T10:30:00.000Z")
    private LocalDateTime datePosted;
    
    @Schema(description = "Load status", example = "POSTED")
    private LoadStatus status;
    
    // Constructors
    public LoadDto() {}
    
    public LoadDto(UUID id, String shipperId, FacilityDto facility, String productType, 
                   String truckType, Integer noOfTrucks, Double weight, String comment, 
                   LocalDateTime datePosted, LoadStatus status) {
        this.id = id;
        this.shipperId = shipperId;
        this.facility = facility;
        this.productType = productType;
        this.truckType = truckType;
        this.noOfTrucks = noOfTrucks;
        this.weight = weight;
        this.comment = comment;
        this.datePosted = datePosted;
        this.status = status;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getShipperId() { return shipperId; }
    public void setShipperId(String shipperId) { this.shipperId = shipperId; }
    
    public FacilityDto getFacility() { return facility; }
    public void setFacility(FacilityDto facility) { this.facility = facility; }
    
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
    
    public String getTruckType() { return truckType; }
    public void setTruckType(String truckType) { this.truckType = truckType; }
    
    public Integer getNoOfTrucks() { return noOfTrucks; }
    public void setNoOfTrucks(Integer noOfTrucks) { this.noOfTrucks = noOfTrucks; }
    
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public LocalDateTime getDatePosted() { return datePosted; }
    public void setDatePosted(LocalDateTime datePosted) { this.datePosted = datePosted; }
    
    public LoadStatus getStatus() { return status; }
    public void setStatus(LoadStatus status) { this.status = status; }
}