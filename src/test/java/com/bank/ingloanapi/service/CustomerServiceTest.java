package com.bank.ingloanapi.service;

import com.bank.ingloanapi.model.Customer;
import com.bank.ingloanapi.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    private AutoCloseable closeable; // For managing resources

    @BeforeEach
    void setUp() {
        // Initialize mocks and store the resource
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Ensure resources are closed
        closeable.close();
    }

    @Test
    void testUpdateUsedCreditLimit() {
        // Arrange
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(BigDecimal.valueOf(10000));  // Initial credit limit
        customer.setUsedCreditLimit(BigDecimal.valueOf(5000));  // Initial used credit limit

        // Simulate repository behavior
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        customerService.updateUsedCreditLimit(customer, 2000); // Update used credit limit

        // Assert
        assertEquals(BigDecimal.valueOf(7000.0), customer.getUsedCreditLimit()); // Used credit limit should now be 7000
        verify(customerRepository, times(1)).save(any(Customer.class));  // Verify save is called
    }
}
