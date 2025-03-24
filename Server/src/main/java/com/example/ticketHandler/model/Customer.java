package com.example.ticketHandler.model;

import com.example.ticketHandler.service.ConfigurationService;

import java.io.IOException;

import com.example.ticketHandler.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Customer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Customer.class);

    private final String id;

    private String name;

    private final TicketPool ticketPool;

    private int interval;

    private boolean active = true;

    private boolean specialAccess = false;

    private int availableTickets = 0;

    private final int saleTicketLimit;

    private final TransactionService transactionService;

    public Customer(TicketPool ticketPool, ConfigurationService configurationService, String id, TransactionService transactionService) throws IOException, ClassNotFoundException {
        this.id = id;
        this.ticketPool = ticketPool;
        this.saleTicketLimit = configurationService.getConfiguration().getCustomerRetrievalRate();
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSpecialAccess() {
        return specialAccess;
    }

    public void setSpecialAccess(boolean specialAccess) {
        this.specialAccess = specialAccess;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(int availableTickets) {
        this.availableTickets = availableTickets;
    }

    public int getSaleTicketLimit() {
        return saleTicketLimit;
    }

    public TransactionService getTransactionHistoryService() {
        return transactionService;
    }

    @Override
    public void run() {
        while (active) {
            try {
                if (ticketPool.areAllTicketsSoldOut()) {
                    logger.info("Customer {}: All tickets have been sold out. Stopping activity.", id);
                    saveTicketHistory();
                    stopCurrentThread();
                    break;
                }

                if (!ticketPool.getTickets().isEmpty() && !ticketPool.areAllTicketsSoldOut()) {
                    if(ticketPool.getTickets().size() < saleTicketLimit) {
                        int balanceTickets = ticketPool.getTickets().size();

                        if (ticketPool.removeTicket(balanceTickets, id)) {
                            availableTickets += balanceTickets;
                            logger.info("Customer {}. Tickets purchased: {}", id, balanceTickets);
                        }
                    } else {
                        if (ticketPool.removeTicket(saleTicketLimit, id)) {
                            availableTickets += saleTicketLimit;
                            logger.info("Customer {}. Tickets purchased: {}", id, saleTicketLimit);
                        }
                    }
                } else
                    logger.warn("Customer {} found no tickets available in the pool.", id);

                if(specialAccess)
                    Thread.sleep((interval * 1000L) / 2);
                else
                    Thread.sleep(interval * 1000L);

            } catch (Exception e) {
                logger.error("Thread was interrupted. {}", e.getMessage(), e);
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
            transactionService.saveTransaction(id.toString(), "CUSTOMER", availableTickets);
            logger.info("Customer {}: Ticket purchase history saved. Total tickets: {}", id, availableTickets);
        } catch (Exception e) {
            logger.error("Customer {}: Failed to save ticket history. Error: {}", id, e.getMessage(), e);
        }
    }
}
