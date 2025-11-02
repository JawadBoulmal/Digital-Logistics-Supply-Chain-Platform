package com.project.supplychain.controller;

import com.project.supplychain.DTOs.purchaseOrderLineDTOs.PurchaseOrderLineDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.services.PurchaseOrderLineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("api/purchaseOrderLines")
public class PurchaseOrderLineController {

    @Autowired
    private PurchaseOrderLineService purchaseOrderLineService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PurchaseOrderLineDTO dto) {
        try {
            HashMap<String, Object> result = purchaseOrderLineService.create(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            HashMap<String, Object> result = purchaseOrderLineService.list();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = purchaseOrderLineService.get(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody PurchaseOrderLineDTO dto) {
        try {
            HashMap<String, Object> result = purchaseOrderLineService.update(id, dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = purchaseOrderLineService.delete(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
