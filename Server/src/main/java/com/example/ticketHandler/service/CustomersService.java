package com.example.ticketHandler.service;

import com.example.ticketHandler.entity.Customers;
import com.example.ticketHandler.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomersService {

    @Autowired
    private CustomersRepository customerRepository;

    public Customers createCustomer(Customers customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customers> getCustomer(UUID customerId) {
        return customerRepository.findById(customerId);
    }

    public List<Customers> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customers updateCustomer(UUID customerId, Customers customerDetails) {
        Optional<Customers> customer = customerRepository.findById(customerId);

        if (!customer.isPresent())
            throw new RuntimeException("Customer not found");

        customer.get().setSpecialAccess(customerDetails.isSpecialAccess());
        customer.get().setAddress(customerDetails.getAddress());
        customer.get().setEmail(customerDetails.getEmail());
        customer.get().setNumber(customerDetails.getNumber());
        customer.get().setName(customerDetails.getName());
        customer.get().setGender(customerDetails.getGender());
        customer.get().setInterval(customerDetails.getInterval());

        return customerRepository.save(customer.get());
    }

    public void deleteCustomer(UUID customerId) {
        customerRepository.deleteById(customerId);
    }
}
