package com.example.ticketHandler.config;

import java.io.Serializable;

public class Configuration implements Serializable {

    private static final long serialVersionUID = 1L;

    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;

    public Configuration() {}

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        if (totalTickets < 0) {
            throw new IllegalArgumentException("Total tickets cannot be negative.");
        }
        this.totalTickets = totalTickets;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public void setTicketReleaseRate(int ticketReleaseRate) {
        if (ticketReleaseRate < 0) {
            throw new IllegalArgumentException("Ticket release rate cannot be negative.");
        }
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public void setCustomerRetrievalRate(int customerRetrievalRate) {
        if (customerRetrievalRate < 0) {
            throw new IllegalArgumentException("Customer retrieval rate cannot be negative.");
        }
        this.customerRetrievalRate = customerRetrievalRate;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) {
        if (maxTicketCapacity < 0) {
            throw new IllegalArgumentException("Max ticket capacity cannot be negative.");
        }
        this.maxTicketCapacity = maxTicketCapacity;
    }
}
