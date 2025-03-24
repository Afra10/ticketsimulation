package com.example.ticketHandler.repository;

import com.example.ticketHandler.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
