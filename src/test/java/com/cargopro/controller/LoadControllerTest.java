package com.cargopro.controller;

import com.cargopro.dto.FacilityDto;
import com.cargopro.dto.LoadDto;
import com.cargopro.enums.LoadStatus;
import com.cargopro.service.LoadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoadController.class)
class LoadControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private LoadService loadService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private LoadDto testLoadDto;
    private UUID testLoadId;
    
    @BeforeEach
    void setUp() {
        testLoadId = UUID.randomUUID();
        
        FacilityDto facilityDto = new FacilityDto("Mumbai", "Delhi", 
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
        
        testLoadDto = new LoadDto(testLoadId, "SHIPPER001", facilityDto, "Electronics", 
            "Container", 2, 15.5, "Test comment", LocalDateTime.now(), LoadStatus.POSTED);
    }
    
    @Test
    void createLoad_ShouldReturnCreatedLoad_WhenValidInput() throws Exception {
        // Given
        when(loadService.createLoad(any(LoadDto.class))).thenReturn(testLoadDto);
        
        // When & Then
        mockMvc.perform(post("/load")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLoadDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testLoadId.toString()))
                .andExpect(jsonPath("$.shipperId").value("SHIPPER001"))
                .andExpect(jsonPath("$.status").value("POSTED"));
    }
    
    @Test
    void createLoad_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given
        LoadDto invalidLoad = new LoadDto();
        invalidLoad.setShipperId(""); // Invalid empty shipper ID
        
        // When & Then
        mockMvc.perform(post("/load")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoad)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getLoads_ShouldReturnPageOfLoads_WhenValidRequest() throws Exception {
        // Given
        Page<LoadDto> loadPage = new PageImpl<>(Arrays.asList(testLoadDto));
        when(loadService.getLoads(eq("SHIPPER001"), eq("Container"), eq(LoadStatus.POSTED), any()))
            .thenReturn(loadPage);
        
        // When & Then
        mockMvc.perform(get("/load")
                .param("shipperId", "SHIPPER001")
                .param("truckType", "Container")
                .param("status", "POSTED")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].shipperId").value("SHIPPER001"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    void getLoadById_ShouldReturnLoad_WhenLoadExists() throws Exception {
        // Given
        when(loadService.getLoadById(testLoadId)).thenReturn(testLoadDto);
        
        // When & Then
        mockMvc.perform(get("/load/{loadId}", testLoadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testLoadId.toString()))
                .andExpect(jsonPath("$.shipperId").value("SHIPPER001"));
    }
    
    @Test
    void updateLoad_ShouldReturnUpdatedLoad_WhenValidInput() throws Exception {
        // Given
        LoadDto updatedLoad = new LoadDto();
        updatedLoad.setShipperId("UPDATED_SHIPPER");
        updatedLoad.setFacility(testLoadDto.getFacility());
        updatedLoad.setProductType("Updated Product");
        updatedLoad.setTruckType("Updated Truck");
        updatedLoad.setNoOfTrucks(3);
        updatedLoad.setWeight(20.0);
        
        when(loadService.updateLoad(eq(testLoadId), any(LoadDto.class))).thenReturn(updatedLoad);
        
        // When & Then
        mockMvc.perform(put("/load/{loadId}", testLoadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedLoad)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipperId").value("UPDATED_SHIPPER"));
    }
    
    @Test
    void deleteLoad_ShouldReturnNoContent_WhenLoadExists() throws Exception {
        // When & Then
        mockMvc.perform(delete("/load/{loadId}", testLoadId))
                .andExpect(status().isNoContent());
    }
}