package com.bank.ingloanapi.service;

import com.bank.ingloanapi.model.LoanInstallment;
import com.bank.ingloanapi.repository.LoanInstallmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoanInstallmentServiceTest {

    @InjectMocks
    private LoanInstallmentService loanInstallmentService;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

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
    void testPayInstallment() {
        // Arrange
        LoanInstallment installment = new LoanInstallment();
        installment.setId(1L);
        installment.setAmount(BigDecimal.valueOf(500));
        installment.setPaidAmount(BigDecimal.ZERO);
        installment.setDueDate(LocalDate.now());
        installment.setIsPaid(false);

        when(loanInstallmentRepository.findById(1L)).thenReturn(Optional.of(installment));
        when(loanInstallmentRepository.save(any(LoanInstallment.class))).thenReturn(installment);

        // Act
        LoanInstallment updatedInstallment = loanInstallmentService.payInstallment(1L, BigDecimal.valueOf(500));

        // Assert
        assertTrue(updatedInstallment.getIsPaid());
        assertEquals(BigDecimal.valueOf(500), updatedInstallment.getPaidAmount());
        verify(loanInstallmentRepository, times(1)).save(any(LoanInstallment.class));
    }
}
