package com.project.supplychain.services;

import com.project.supplychain.DTOs.warehouseDTOs.WarehouseDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.WarehouseMapper;
import com.project.supplychain.models.Warehouse;
import com.project.supplychain.models.user.User;
import com.project.supplychain.models.user.WarehouseManager;
import com.project.supplychain.repositories.UserRepository;
import com.project.supplychain.repositories.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WarehouseMapper warehouseMapper;

    public HashMap<String, Object> create(WarehouseDTO dto) {
        Warehouse entity = warehouseMapper.toEntity(dto);
        WarehouseManager manager;

        if (dto.getWarehouseManagerId() != null) {
             User user = userRepository.findById(dto.getWarehouseManagerId())
                    .orElseThrow(() -> new BadRequestException("Warehouse manager not found"));
            if (!(user instanceof WarehouseManager wm)) {
                throw new BadRequestException("Provided user is not a warehouse manager");
            }
            manager = wm;
        }else{
            throw new BadRequestException("The warehouse manager is required");
        }
        if(warehouseRepository.existsByCode(dto.getCode())){
            throw new BadRequestException("The warehouse code is already exist .");
        }
        entity.setWarehouseManager(manager);
        Warehouse savedWarehouse = warehouseRepository.save(entity);

        manager.getWarehouses().add(savedWarehouse);
        userRepository.save(manager);

        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Warehouse created successfully");
        result.put("warehouse", warehouseMapper.toDTO(savedWarehouse));
        return result;
    }

    public HashMap<String, Object> get(UUID id) {
        Warehouse found = warehouseRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Warehouse not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("warehouse", warehouseMapper.toDTO(found));
        return result;
    }

    public HashMap<String, Object> list() {
        List<WarehouseDTO> list = warehouseRepository.findAll()
                .stream()
                .map(warehouseMapper::toDTO)
                .toList();
        HashMap<String, Object> result = new HashMap<>();
        result.put("warehouses", list);
        return result;
    }

    public HashMap<String, Object> update(UUID id, WarehouseDTO dto) {
        Warehouse existing = warehouseRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Warehouse not found"));

        existing.setCode(dto.getCode());
        existing.setName(dto.getName());
        existing.setActive(dto.isActive());

        if (dto.getWarehouseManagerId() != null) {
            var user = userRepository.findById(dto.getWarehouseManagerId())
                    .orElseThrow(() -> new BadRequestException("Warehouse manager not found"));
            if (!(user instanceof WarehouseManager manager)) {
                throw new BadRequestException("Provided user is not a warehouse manager");
            }
            existing.setWarehouseManager(manager);
        } else {
            existing.setWarehouseManager(null);
        }

        Warehouse saved = warehouseRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Warehouse updated successfully");
        result.put("warehouse", warehouseMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> delete(UUID id) {
        Warehouse existing = warehouseRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Warehouse not found"));
        warehouseRepository.delete(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Warehouse deleted successfully");
        return result;
    }
}
