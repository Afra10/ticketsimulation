package com.example.ticketHandler.service;

import com.example.ticketHandler.entity.Vendors;
import com.example.ticketHandler.repository.VendorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VendorsService {

    @Autowired
    private VendorsRepository vendorsRepository;

    public Vendors createVendor(Vendors vendor) {
        return vendorsRepository.save(vendor);
    }

    public Optional<Vendors> getVendor(UUID id) {
        return vendorsRepository.findById(id);
    }

    public List<Vendors> getAllVendors() {
        return vendorsRepository.findAll();
    }

    public Vendors updateVendor(UUID id, Vendors vendorDetails) {
        Optional<Vendors> vendor = vendorsRepository.findById(id);

        if (!vendor.isPresent())
            throw new RuntimeException("Vendor not found");

        vendor.get().setName(vendorDetails.getName());
        vendor.get().setAddress(vendorDetails.getAddress());
        vendor.get().setEmail(vendorDetails.getEmail());
        vendor.get().setNumber(vendorDetails.getNumber());
        vendor.get().setGender(vendorDetails.getGender());
        vendor.get().setInterval(vendorDetails.getInterval());

        return vendorsRepository.save(vendor.get());
    }

    public void deleteVendor(UUID id) {
        vendorsRepository.deleteById(id);
    }
}
