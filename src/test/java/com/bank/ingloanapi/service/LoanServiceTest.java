package com.bank.ingloanapi.service;

import com.bank.ingloanapi.model.Customer;
import com.bank.ingloanapi.model.Loan;
import com.bank.ingloanapi.repository.LoanRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoanServiceTest {

    @InjectMocks
    private LoanService loanService;

    @Mock
    private LoanRepository loanRepository;

    private AutoCloseable closeable; // For managing resources

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // Ensure resources are cleaned up
    }

    @Test
    void testCreateLoan() {
        // Arrange
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setCreditLimit(BigDecimal.valueOf(10000));
        customer.setUsedCreditLimit(BigDecimal.ZERO);

        Loan loan = new Loan();
        loan.setLoanAmount(BigDecimal.valueOf(5000));
        loan.setNumberOfInstallments(12);
        loan.setInterestRate(BigDecimal.valueOf(5));
        loan.setCustomer(customer);

        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        // Act
        Loan createdLoan = loanService.createLoan(loan);

        // Assert
        assertNotNull(createdLoan);
        assertEquals(BigDecimal.valueOf(5000), createdLoan.getLoanAmount());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void testFindLoanById() {
        // Arrange
        Loan loan = new Loan();
        loan.setId(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        // Act
        Optional<Loan> foundLoanOptional = loanService.findLoanById(1L);

        // Assert
        assertNotNull(foundLoanOptional);
        Loan foundLoan = foundLoanOptional.orElseThrow();
        assertEquals(1L, foundLoan.getId());
        verify(loanRepository, times(1)).findById(1L);
    }
}
