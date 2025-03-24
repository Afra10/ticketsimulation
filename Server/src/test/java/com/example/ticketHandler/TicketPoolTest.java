package com.example.ticketHandler;

import com.example.ticketHandler.config.Configuration;
import com.example.ticketHandler.model.TicketPool;
import com.example.ticketHandler.service.ConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TicketPoolTest {

    private ConfigurationService configurationService;
    private TicketPool ticketPool;

    private Configuration mockConfig = new Configuration();

    @BeforeEach
    public void setUp() throws IOException, ClassNotFoundException {
        configurationService = Mockito.mock(ConfigurationService.class);

        Configuration mockConfig = new Configuration();
        mockConfig.setTotalTickets(100);
        mockConfig.setTicketReleaseRate(7);
        mockConfig.setCustomerRetrievalRate(5);
        mockConfig.setMaxTicketCapacity(25);

        Mockito.when(configurationService.getConfiguration()).thenReturn(mockConfig);

        ticketPool = new TicketPool(configurationService);
    }

    @Test
    public void testTicketQueueInitialization() {
        assertNotNull(ticketPool, "Ticket Queue should be initialized");
        assertEquals(100, ticketPool.getTotalTicketStock(), "Total tickets should be 100");
    }
    @Test
    public void testGetConfigurationFailure() throws Exception {
        Mockito.when(configurationService.getConfiguration()).thenThrow(new IOException("Configuration file not found"));

        assertThrows(IOException.class, () -> {
            configurationService.getConfiguration();
        });
    }

    @Test
    public void testStoreConfiguration() throws Exception {
        Configuration configToSave = new Configuration();
        configToSave.setTotalTickets(100);
        configToSave.setTicketReleaseRate(7);
        configToSave.setCustomerRetrievalRate(5);
        configToSave.setMaxTicketCapacity(25);

        Mockito.doNothing().when(configurationService).storeConfiguration(configToSave);

        configurationService.storeConfiguration(configToSave);

        Mockito.verify(configurationService, Mockito.times(1)).storeConfiguration(configToSave);
    }

    @Test
    public void testTicketQueueMaximumCapacity() {
        int customers = 150;
        for (int i = 0; i < customers; i++) {
            ticketPool.addTickets(4, "Test");
        }

        assertEquals(20, ticketPool.getTickets().size());
    }

    @Test
    public void testTicketQueueMinimumCapacity() {
        int customers = 150;
        for (int i = 0; i < customers; i++) {
            ticketPool.removeTicket(4, "Test");
        }

        assertEquals(0, ticketPool.getTickets().size());
    }
}
