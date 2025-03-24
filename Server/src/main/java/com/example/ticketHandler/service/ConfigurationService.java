package com.example.ticketHandler.service;

import com.example.ticketHandler.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Properties;

@Service
public class ConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);
    private static final String FILE_PATH = "D:\\TV Series\\Server-20241130T160943Z-001\\Server\\src\\main\\resources\\config.properties";

    public Configuration getConfiguration() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            logger.error("Configuration file not found: {}", FILE_PATH);
            throw new FileNotFoundException("Configuration file is missing.");
        }

        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
            logger.info("Configuration loaded successfully.");

            Configuration configuration = new Configuration();
            configuration.setTotalTickets(Integer.parseInt(properties.getProperty("totalTickets", "0")));
            configuration.setTicketReleaseRate(Integer.parseInt(properties.getProperty("ticketReleaseRate", "0")));
            configuration.setCustomerRetrievalRate(Integer.parseInt(properties.getProperty("customerRetrievalRate", "0")));
            configuration.setMaxTicketCapacity(Integer.parseInt(properties.getProperty("maxTicketCapacity", "0")));
            return configuration;
        } catch (Exception e) {
            logger.error("Error loading configuration: {}", e.getMessage(), e);
            throw new IOException("Failed to load configuration.", e);
        }
    }

    public void storeConfiguration(Configuration configuration) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("totalTickets", String.valueOf(configuration.getTotalTickets()));
        properties.setProperty("ticketReleaseRate", String.valueOf(configuration.getTicketReleaseRate()));
        properties.setProperty("customerRetrievalRate", String.valueOf(configuration.getCustomerRetrievalRate()));
        properties.setProperty("maxTicketCapacity", String.valueOf(configuration.getMaxTicketCapacity()));

        File file = new File(FILE_PATH);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            properties.store(fileOutputStream, "Ticket System Configuration");
            logger.info("Configuration saved successfully to {}", FILE_PATH);
        } catch (Exception e) {
            logger.error("Error saving configuration: {}", e.getMessage(), e);
            throw new IOException("Failed to save configuration.", e);
        }
    }
}
