package com.example.ticketHandler.controller;

import com.example.ticketHandler.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/start-ticket-simulation")
    public String startSimulation() throws IOException, ClassNotFoundException {
        return ticketService.startSimulation();
    }

    @PostMapping("/stop-ticket-simulation")
    public String stopSimulation() {
        return ticketService.stopSimulation();
    }

    @PostMapping("/stop-active-vendor/{id}")
    public String stopVendor(@PathVariable UUID id) {
        return ticketService.stopVendorById(id.toString());
    }

    @PostMapping("/stop-active-customer/{id}")
    public String stopCustomer(@PathVariable UUID id) {
        return ticketService.stopCustomerById(id.toString());
    }

    @PostMapping("/reset-ticket-pool")
    public String resetTicketPool() {
        return ticketService.resetTicketPool();
    }

    @GetMapping("/customers")
    public List<Map<String, Object>> retrieveAllActiveCustomers() {
        return ticketService.retrieveAllActiveCustomers();
    }

    @GetMapping("/vendors")
    public List<Map<String, Object>> retrieveAllActiveVendors() {
        return ticketService.retrieveAllActiveVendors();
    }

    @GetMapping("/ticket-pool-details")
    public Map<String, Integer> retrieveTicketPoolStatus() {
        return ticketService.retrieveTicketPoolStatus();
    }
}
