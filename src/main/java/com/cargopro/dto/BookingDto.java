package com.cargopro.dto;

import com.cargopro.enums.BookingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Booking Data Transfer Object")
public class BookingDto {
    
    @Schema(description = "Booking ID", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID id;
    
    @NotNull(message = "Load ID is required")
    @Schema(description = "Load ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID loadId;
    
    @NotBlank(message = "Transporter ID is required")
    @Schema(description = "Transporter ID", example = "TRANS001")
    private String transporterId;
    
    @NotNull(message = "Proposed rate is required")
    @Positive(message = "Proposed rate must be positive")
    @Schema(description = "Proposed rate", example = "25000.0")
    private Double proposedRate;
    
    @Schema(description = "Additional comments", example = "Can deliver within 2 days")
    private String comment;
    
    @Schema(description = "Booking status", example = "PENDING")
    private BookingStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Schema(description = "Request timestamp", example = "2024-01-15T11:30:00.000Z")
    private LocalDateTime requestedAt;
    
    // Constructors
    public BookingDto() {}
    
    public BookingDto(UUID id, UUID loadId, String transporterId, Double proposedRate, 
                     String comment, BookingStatus status, LocalDateTime requestedAt) {
        this.id = id;
        this.loadId = loadId;
        this.transporterId = transporterId;
        this.proposedRate = proposedRate;
        this.comment = comment;
        this.status = status;
        this.requestedAt = requestedAt;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getLoadId() { return loadId; }
    public void setLoadId(UUID loadId) { this.loadId = loadId; }
    
    public String getTransporterId() { return transporterId; }
    public void setTransporterId(String transporterId) { this.transporterId = transporterId; }
    
    public Double getProposedRate() { return proposedRate; }
    public void setProposedRate(Double proposedRate) { this.proposedRate = proposedRate; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
}