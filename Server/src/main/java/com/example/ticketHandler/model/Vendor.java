package com.example.ticketHandler.model;

import com.example.ticketHandler.service.ConfigurationService;

import java.io.IOException;

import com.example.ticketHandler.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vendor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Vendor.class);

    private final String id;

    private String name;

    private final TicketPool ticketPool;

    private int interval;

    private final int saleTicketLimit;

    private int availableTickets = 0;

    private final int totalTicket;

    private boolean active = true;

    private final TransactionService transactionService;

    public Vendor(TicketPool ticketPool, ConfigurationService configurationService, String id, TransactionService transactionService) throws IOException, ClassNotFoundException {
        this.id = id;
        this.ticketPool = ticketPool;
        this.saleTicketLimit = configurationService.getConfiguration().getTicketReleaseRate();
        this.totalTicket = configurationService.getConfiguration().getTotalTickets();
        this.transactionService = transactionService;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TicketPool getTicketPool() {
        return ticketPool;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getSaleTicketLimit() {
        return saleTicketLimit;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(int availableTickets) {
        this.availableTickets = availableTickets;
    }

    public int getTotalTicket() {
        return totalTicket;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public TransactionService getTransactionHistoryService() {
        return transactionService;
    }

    @Override
    public void run() {
        while (active) {
            try {
                if (ticketPool.isStockDepleted()) {
                    logger.info("Vendor {}: All tickets are sold out. Stopping ticket additions.", id);
                    saveTicketHistory();
                    stopCurrentThread();
                    break;
                }

                if (!ticketPool.isCapacityFull()) {
                    if(ticketPool.isBelowReleaseThreshold()) {
                        int ticCount = ticketPool.getTotalTicketStock() - ticketPool.getTotalTicketsInserted();
                        ticketPool.addTickets(ticCount, id);
                        availableTickets += ticCount;

                        logger.info("Vendor {}: Successfully added {} tickets to the pool. Remaining tickets: {}", id, ticCount, ticketPool.getTickets().size());
                    } else {
                        ticketPool.addTickets(saleTicketLimit, id);
                        availableTickets += saleTicketLimit;

                        logger.info("Vendor {}: Successfully added {} tickets to the pool. Current pool size: {}", id, saleTicketLimit, ticketPool.getTickets().size());
                    }
                } else
                    logger.warn("Vendor {}: Ticket pool is at maximum capacity. Cannot add more tickets.", id);

                Thread.sleep(interval * 1000L);
            } catch (Exception e) {
                logger.error("Vendor thread was interrupted: {}", e.getMessage(), e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void stopCurrentThread() {
        this.active = false;
        Thread.currentThread().interrupt();
    }

    private void saveTicketHistory() {
        try {
            transactionService.saveTransaction(id.toString(), "VENDOR", availableTickets);
            logger.info("Vendor {}: Ticket addition history successfully saved. Total tickets added: {}", id, availableTickets);
        } catch (Exception e) {
            logger.error("Vendor {}: Failed to save ticket addition history. Error: {}", id, e.getMessage(), e);
        }
    }
}
