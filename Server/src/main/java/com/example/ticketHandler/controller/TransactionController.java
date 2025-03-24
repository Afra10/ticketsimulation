package com.example.ticketHandler.controller;

import com.example.ticketHandler.entity.Transaction;
import com.example.ticketHandler.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactionHistory() {
        return ResponseEntity.ok(transactionService.getAllTransactionHistory());
    }
}
