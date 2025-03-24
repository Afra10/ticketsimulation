package com.example.ticketHandler.repository;

import com.example.ticketHandler.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CustomersRepository extends JpaRepository<Customers, UUID> {
}
