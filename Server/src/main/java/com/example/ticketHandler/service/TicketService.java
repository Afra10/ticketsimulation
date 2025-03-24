package com.example.ticketHandler.service;

import com.example.ticketHandler.entity.Customers;
import com.example.ticketHandler.entity.Vendors;
import com.example.ticketHandler.model.Customer;
import com.example.ticketHandler.model.TicketPool;
import com.example.ticketHandler.model.Vendor;
import com.example.ticketHandler.repository.CustomersRepository;
import com.example.ticketHandler.repository.VendorsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private VendorsRepository vendorsRepository;

    @Autowired
    private TransactionService transactionService;

    private final TicketPool ticketPool;

    private final ConfigurationService configurationService;

    private final VendorsRepository vendorRepository;

    private final CustomersRepository customerRepository;

    private final PriorityQueue<Customer> customerQueue;

    private Map<String, Customer> activeCustomers  = new HashMap<>();

    private Map<String, Vendor> activeVendors  = new HashMap<>();

    private List<Thread> vendorTaskThreads = new ArrayList<>();

    private List<Thread> customerTaskThreads = new ArrayList<>();

    public TicketService(TicketPool ticketPool, ConfigurationService configurationService,
                         VendorsRepository vendorRepository, CustomersRepository customerRepository) {
        this.ticketPool = ticketPool;
        this.configurationService = configurationService;
        this.vendorRepository = vendorRepository;
        this.customerRepository = customerRepository;

        this.customerQueue = new PriorityQueue<>(Comparator.comparing(Customer::isSpecialAccess).reversed());

        logger.info("TicketService initialized with the provided TicketPool and ConfigurationService.");
    }

    public String startSimulation() throws IOException, ClassNotFoundException {
        logger.info("Starting the ticket simulation...");

        List<Vendors> vendorsList = vendorRepository.findAll();
        logger.info("Found {} vendors in the repository.", vendorsList.size());

        for (Vendors ven : vendorsList) {
            Vendor vendor = new Vendor(ticketPool, configurationService, ven.getId().toString(), transactionService);
            vendor.setName(ven.getName());
            vendor.setInterval(ven.getInterval());

            Thread vendorThread = new Thread(vendor);
            vendorTaskThreads.add(vendorThread);
            vendorThread.start();

            activeVendors .put(vendor.getId(), vendor);
        }

        List<Customers> customersList = customerRepository.findAll();

        for (Customers cus : customersList) {
            Customer customer = new Customer(ticketPool, configurationService, cus.getId().toString(), transactionService);
            customer.setName(cus.getName());
            customer.setInterval(cus.getInterval());

            if (cus.isSpecialAccess()) {
                customer.setSpecialAccess(true);
            }

            customerQueue.add(customer);
            logger.info("Customer {} ({}) added to the queue with special access: {}.", customer.getId(), customer.getName(), customer.isSpecialAccess());
        }

        while (!customerQueue.isEmpty()) {
            Customer customer = customerQueue.poll();

            Thread customerThread = new Thread(customer);
            customerTaskThreads.add(customerThread);
            customerThread.start();

            activeCustomers .put(customer.getId(), customer);
            logger.info("Customer {} ({}) started successfully.", customer.getId(), customer.getName());
        }

        logger.info("Simulation started successfully with {} vendors and {} customers.", vendorsList.size(), customersList.size());
        return "Simulation started with " + vendorsList.size() + " active vendors and " + customersList.size() + " active customers.";
    }

    public String stopSimulation() {
        logger.info("Stopping the ticket simulation...");

        for (Thread vendorThread : vendorTaskThreads) {
            if (vendorThread != null && vendorThread.isAlive()) {
                vendorThread.interrupt();
            }
        }
        logger.info("Vendor thread interrupted.");

        for (Thread customerThread : customerTaskThreads) {
            if (customerThread != null && customerThread.isAlive()) {
                customerThread.interrupt();
            }
        }
        logger.info("Customer thread interrupted.");

        vendorTaskThreads.clear();
        customerTaskThreads.clear();
        activeCustomers .clear();

        ticketPool.setTotalTicketsInserted(0);
        ticketPool.setTotalTicketsExtracted(0);
        ticketPool.resetTicketPool();

        logger.info("Simulation stopped successfully. All threads interrupted and pool reset.");
        return "Simulation stopped, all threads interrupted.";
    }

    public List<Map<String, Object>> retrieveAllActiveCustomers() {
        if(ticketPool.getTotalTicketsInserted() == 0)
            return Collections.emptyList();

        List<Customers> allCustomers = customerRepository.findAll();
        Map<String, Integer> customerTicketMap = ticketPool.getExtractedTicketCounts();
        List<Map<String, Object>> vendorDetailsList = new ArrayList<>();

        for (Customers customer : allCustomers) {
            String id = customer.getId().toString();
            String customerName = customer.getName();

            int availableTickets = customerTicketMap.getOrDefault(id, 0);

            boolean status = false;
            if(activeCustomers.get(id) != null)
                status = activeCustomers.get(id).isActive();

            Map<String, Object> details = new HashMap<>();
            details.put("id", id);
            details.put("name", customerName);
            details.put("availableTickets", availableTickets);
            details.put("status", status);

            vendorDetailsList.add(details);
        }

        return vendorDetailsList;
    }

    public List<Map<String, Object>> retrieveAllActiveVendors() {
        if(ticketPool.getTotalTicketsInserted() == 0)
            return Collections.emptyList();

        List<Vendors> allVendors = vendorsRepository.findAll();
        Map<String, Integer> vendorTicketMap = ticketPool.getInsertedTicketCounts();
        List<Map<String, Object>> vendorDetailsList = new ArrayList<>();

        for (Vendors vendor : allVendors) {
            String id = vendor.getId().toString();
            String vendorName = vendor.getName();

            int availableTickets = vendorTicketMap.getOrDefault(id, 0);

            boolean status = false;
            if(activeVendors.get(id) != null)
                status = activeVendors.get(id).isActive();

            Map<String, Object> details = new HashMap<>();
            details.put("id", id);
            details.put("name", vendorName);
            details.put("availableTickets", availableTickets);
            details.put("status", status);

            vendorDetailsList.add(details);
        }

        return vendorDetailsList;
    }

    public String stopCustomerById(String customerId) {
        if (activeCustomers.get(customerId).isActive()) {
            activeCustomers.get(customerId).setActive(false);
            return "Customer " + customerId + " stopped successfully.";
        }

        return "Customer " + customerId + " not found or already stopped.";
    }

    public String stopVendorById(String vendorId) {
        if (activeVendors.get(vendorId).isActive()) {
            activeVendors.get(vendorId).setActive(false);
            return "Vendor " + vendorId + " stopped successfully.";
        }

        return "Vendor " + vendorId + " not found or already stopped.";
    }

    public Map<String, Integer> retrieveTicketPoolStatus() {
        Map<String, Integer> poolDetails = new HashMap<>();

        poolDetails.put("totalTickets", ticketPool.getTotalTicketStock());
        poolDetails.put("maximumPoolSize", ticketPool.getMaxAllowedTickets());
        poolDetails.put("activeCustomers", activeCustomers .size());
        poolDetails.put("availableTicketsInPool", ticketPool.getTickets().size());
        poolDetails.put("ticketsInserted", ticketPool.getTotalTicketsInserted());
        poolDetails.put("ticketsExtracted", ticketPool.getTotalTicketsExtracted());
        poolDetails.put("totalAvailableTickets", ticketPool.getTotalTicketStock() - ticketPool.getTotalTicketsInserted());

        return poolDetails;
    }

    public String resetTicketPool() {
        stopSimulation();

        ticketPool.setTotalTicketsInserted(0);
        ticketPool.setTotalTicketsExtracted(0);
        ticketPool.resetTicketPool();

        activeCustomers .clear();
        activeVendors .clear();
        customerQueue.clear();

        logger.info("TicketService and TicketPool successfully reset.");
        return "TicketService and TicketPool successfully reset.";
    }

}
