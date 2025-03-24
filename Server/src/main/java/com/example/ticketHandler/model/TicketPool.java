package com.example.ticketHandler.model;

import com.example.ticketHandler.service.ConfigurationService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TicketPool {

    private static final Logger logger = LoggerFactory.getLogger(TicketPool.class);

    private final ConfigurationService configurationService;

    private final List<Ticket> tickets = Collections.synchronizedList(new Vector<>());

    private int maxAllowedTickets;

    private int totalTicketStock;

    private int saleTicketLimit;

    private int totalTicketsInserted = 0;

    private int totalTicketsExtracted = 0;

    private final Map<String, Integer> insertedTicketCounts;
    
    private final Map<String, Integer> extractedTicketCounts;

    public TicketPool(ConfigurationService configurationService) throws IOException, ClassNotFoundException {
        this.configurationService = configurationService;
        this.maxAllowedTickets = this.configurationService.getConfiguration().getMaxTicketCapacity();
        this.totalTicketStock = this.configurationService.getConfiguration().getTotalTickets();
        this.saleTicketLimit = configurationService.getConfiguration().getTicketReleaseRate();

        this.insertedTicketCounts = new HashMap<>();
        this.extractedTicketCounts = new HashMap<>();

        logger.info("Ticket pool set up with max capacity: {} and total tickets: {}", maxAllowedTickets, totalTicketStock);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public int getMaxAllowedTickets() {
        return maxAllowedTickets;
    }

    public void setMaxAllowedTickets(int maxAllowedTickets) {
        this.maxAllowedTickets = maxAllowedTickets;
    }

    public int getTotalTicketStock() {
        return totalTicketStock;
    }

    public void setTotalTicketStock(int totalTicketStock) {
        this.totalTicketStock = totalTicketStock;
    }

    public int getSaleTicketLimit() {
        return saleTicketLimit;
    }

    public void setSaleTicketLimit(int saleTicketLimit) {
        this.saleTicketLimit = saleTicketLimit;
    }

    public int getTotalTicketsInserted() {
        return totalTicketsInserted;
    }

    public void setTotalTicketsInserted(int totalTicketsInserted) {
        this.totalTicketsInserted = totalTicketsInserted;
    }

    public int getTotalTicketsExtracted() {
        return totalTicketsExtracted;
    }

    public void setTotalTicketsExtracted(int totalTicketsExtracted) {
        this.totalTicketsExtracted = totalTicketsExtracted;
    }

    public Map<String, Integer> getInsertedTicketCounts() {
        return insertedTicketCounts;
    }

    public Map<String, Integer> getExtractedTicketCounts() {
        return extractedTicketCounts;
    }

    public synchronized void addTickets(int numberOfTickets, String id) {
        try {
            updateTicketConfiguration();
        } catch (Exception e) {
            logger.error("Failed to sync ticket configuration: {}", e.getMessage(), e);
        }

        if (totalTicketsInserted >= totalTicketStock) {
            logger.warn("Ticket addition limit reached. No more tickets can be added.");
            return;
        }

        if (!isCapacityFull()) {
            for (int i = 0; i < numberOfTickets; i++) {
                tickets.add(new Ticket());
            }

            totalTicketsInserted += numberOfTickets;
            insertedTicketCounts.merge(id, numberOfTickets, Integer::sum);

            logger.info("Added {} tickets to the pool. Current pool size: {}", numberOfTickets, tickets.size());
        } else
            logger.warn("Ticket pool is at full capacity. Max capacity: {}", maxAllowedTickets);

    }

    public synchronized boolean removeTicket(int numberOfTickets, String id) {
        if (!tickets.isEmpty()) {
            for (int i = 0; i < numberOfTickets; i++) {
                if(tickets.isEmpty())
                    logger.info("Ticket Pool is empty");
                else {
                    try {
                        if(areAllTicketsSoldOut())
                            return true;
                    } catch (Exception e) {
                        logger.info("Stop request received, halting ticket release.");
                    }

                    tickets.remove(0);
                    totalTicketsExtracted++;
                    extractedTicketCounts.merge(id, 1, Integer::sum);
                }
            }

            logger.info("Tickets successfully removed. Current pool size: {}", tickets.size());
            return true;
        } else {
            logger.warn("Ticket pool is empty, unable to remove any tickets.");
            return false;
        }
    }

    public void resetTicketPool() {
        tickets.clear();
        insertedTicketCounts.clear();
        extractedTicketCounts.clear();
    }

    public boolean isBelowReleaseThreshold() throws IOException, ClassNotFoundException {
        return totalTicketsInserted + configurationService.getConfiguration().getTicketReleaseRate() >= totalTicketStock;
    }

    public boolean areAllTicketsSoldOut() throws IOException, ClassNotFoundException {
        return totalTicketsExtracted>= totalTicketStock;
    }

    public void updateTicketConfiguration() throws IOException, ClassNotFoundException {
        if(maxAllowedTickets != configurationService.getConfiguration().getMaxTicketCapacity() ||
                totalTicketStock != configurationService.getConfiguration().getTotalTickets() ||
                saleTicketLimit != configurationService.getConfiguration().getTicketReleaseRate()) {

            this.maxAllowedTickets = this.configurationService.getConfiguration().getMaxTicketCapacity();
            this.totalTicketStock = this.configurationService.getConfiguration().getTotalTickets();
            this.saleTicketLimit = configurationService.getConfiguration().getTicketReleaseRate();

            logger.info("Ticket pool configuration updated. Max capacity: {} and total ticket stock: {}", maxAllowedTickets, totalTicketStock);
        }
    }

    public boolean isCapacityFull() {
        return (tickets.size() + saleTicketLimit) >= maxAllowedTickets;
    }

    public boolean isStockDepleted() throws IOException, ClassNotFoundException {
        return (totalTicketsInserted) >= totalTicketStock;
    }
}
