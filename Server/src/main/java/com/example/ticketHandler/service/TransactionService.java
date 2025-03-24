package com.example.ticketHandler.service;

import com.example.ticketHandler.entity.Transaction;
import com.example.ticketHandler.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public void saveTransaction(String customerOrVendorId, String type, int availableTickets) {
        Transaction history = new Transaction();
        history.setCustomerOrVendorId(customerOrVendorId);
        history.setType(type);
        history.setAvailableTickets(availableTickets);
        history.setTimestamp(System.currentTimeMillis());

        transactionRepository.save(history);
    }

    public List<Transaction> getAllTransactionHistory() {
        return transactionRepository.findAll();
    }
}
