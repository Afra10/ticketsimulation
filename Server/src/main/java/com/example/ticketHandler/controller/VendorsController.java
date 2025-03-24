package com.example.ticketHandler.controller;

import com.example.ticketHandler.entity.Vendors;
import com.example.ticketHandler.service.VendorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/vendors")
public class VendorsController {

    @Autowired
    private VendorsService vendorsService;

    @PostMapping
    public ResponseEntity<Vendors> createVendor(@RequestBody Vendors vendor) {
        Vendors createdVendor = vendorsService.createVendor(vendor);
        return new ResponseEntity<>(createdVendor, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vendors> getVendor(@PathVariable UUID id) {
        Optional<Vendors> vendor = vendorsService.getVendor(id);

        if(vendor.isPresent())
            return ResponseEntity.ok(vendor.get());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping
    public ResponseEntity<List<Vendors>> getAllVendors() {
        List<Vendors> vendors = vendorsService.getAllVendors();
        return ResponseEntity.ok(vendors);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vendors> updateVendor(@PathVariable UUID id,
                                                @RequestBody Vendors vendor) {
        Vendors updatedVendor = vendorsService.updateVendor(id, vendor);
        return ResponseEntity.ok(updatedVendor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable UUID id) {
        vendorsService.deleteVendor(id);
        return ResponseEntity.noContent().build();
    }
}
