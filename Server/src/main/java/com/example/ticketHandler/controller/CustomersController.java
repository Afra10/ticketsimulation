package com.example.ticketHandler.controller;

import com.example.ticketHandler.entity.Customers;
import com.example.ticketHandler.service.CustomersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomersController {

    @Autowired
    private CustomersService customersService;

    @PostMapping
    public ResponseEntity<Customers> createCustomer(@RequestBody Customers customer) {
        Customers createdCustomer = customersService.createCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customers> getCustomer(@PathVariable UUID id) {
        Optional<Customers> customer = customersService.getCustomer(id);

        if (customer.isPresent())
            return ResponseEntity.ok(customer.get());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @GetMapping
    public ResponseEntity<List<Customers>> getAllCustomers() {
        List<Customers> customers = customersService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customers> updateCustomer(@PathVariable UUID id,
                                                    @RequestBody Customers customer) {
        Customers updatedCustomer = customersService.updateCustomer(id, customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customersService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
