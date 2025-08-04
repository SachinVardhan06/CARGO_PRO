package com.cargopro.mapper;

import com.cargopro.dto.FacilityDto;
import com.cargopro.dto.LoadDto;
import com.cargopro.entity.Facility;
import com.cargopro.entity.Load;
import org.springframework.stereotype.Component;

@Component
public class LoadMapper {
    
    public LoadDto toDto(Load load) {
        if (load == null) {
            return null;
        }
        
        FacilityDto facilityDto = null;
        if (load.getFacility() != null) {
            facilityDto = new FacilityDto(
                load.getFacility().getLoadingPoint(),
                load.getFacility().getUnloadingPoint(),
                load.getFacility().getLoadingDate(),
                load.getFacility().getUnloadingDate()
            );
        }
        
        return new LoadDto(
            load.getId(),
            load.getShipperId(),
            facilityDto,
            load.getProductType(),
            load.getTruckType(),
            load.getNoOfTrucks(),
            load.getWeight(),
            load.getComment(),
            load.getDatePosted(),
            load.getStatus()
        );
    }
    
    public Load toEntity(LoadDto loadDto) {
        if (loadDto == null) {
            return null;
        }
        
        Load load = new Load();
        load.setId(loadDto.getId());
        load.setShipperId(loadDto.getShipperId());
        load.setProductType(loadDto.getProductType());
        load.setTruckType(loadDto.getTruckType());
        load.setNoOfTrucks(loadDto.getNoOfTrucks());
        load.setWeight(loadDto.getWeight());
        load.setComment(loadDto.getComment());
        load.setDatePosted(loadDto.getDatePosted());
        load.setStatus(loadDto.getStatus());
        
        if (loadDto.getFacility() != null) {
            Facility facility = new Facility(
                loadDto.getFacility().getLoadingPoint(),
                loadDto.getFacility().getUnloadingPoint(),
                loadDto.getFacility().getLoadingDate(),
                loadDto.getFacility().getUnloadingDate()
            );
            load.setFacility(facility);
        }
        
        return load;
    }
    
    public void updateEntityFromDto(LoadDto loadDto, Load load) {
        if (loadDto == null || load == null) {
            return;
        }
        
        load.setShipperId(loadDto.getShipperId());
        load.setProductType(loadDto.getProductType());
        load.setTruckType(loadDto.getTruckType());
        load.setNoOfTrucks(loadDto.getNoOfTrucks());
        load.setWeight(loadDto.getWeight());
        load.setComment(loadDto.getComment());
        
        if (loadDto.getFacility() != null) {
            if (load.getFacility() == null) {
                load.setFacility(new Facility());
            }
            load.getFacility().setLoadingPoint(loadDto.getFacility().getLoadingPoint());
            load.getFacility().setUnloadingPoint(loadDto.getFacility().getUnloadingPoint());
            load.getFacility().setLoadingDate(loadDto.getFacility().getLoadingDate());
            load.getFacility().setUnloadingDate(loadDto.getFacility().getUnloadingDate());
        }
    }
}