package com.bank.ingloanapi.controller;

import com.bank.ingloanapi.model.Customer;
import com.bank.ingloanapi.model.Loan;
import com.bank.ingloanapi.model.LoanInstallment;
import com.bank.ingloanapi.service.CustomerService;
import com.bank.ingloanapi.service.LoanInstallmentService;
import com.bank.ingloanapi.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LoanInstallmentService installmentService;

    @PostMapping
    public ResponseEntity<String> createLoan(
            @RequestParam Long customerId,
            @RequestParam double amount,
            @RequestParam double interestRate,
            @RequestParam int numberOfInstallments) {

        if (!Set.of(6, 9, 12, 24).contains(numberOfInstallments)) {
            return ResponseEntity.badRequest().body("Invalid number of installments. Allowed: 6, 9, 12, 24.");
        }

        if (interestRate < 0.1 || interestRate > 0.5) {
            return ResponseEntity.badRequest().body("Invalid interest rate. Allowed range: 0.1 - 0.5.");
        }

        Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Customer not found.");
        }

        Customer customer = customerOpt.get();
        double totalAmount = amount * (1 + interestRate);

        if (!customerService.hasEnoughCreditLimit(customer, totalAmount)) {
            return ResponseEntity.badRequest().body("Insufficient credit limit.");
        }

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(BigDecimal.valueOf(totalAmount));
        loan.setNumberOfInstallments(numberOfInstallments);
        loan.setCreateDate(LocalDate.now());
        loan.setIsPaid(false);

        Loan createdLoan = loanService.createLoan(loan);
        customerService.updateUsedCreditLimit(customer, totalAmount);

        for (int i = 0; i < numberOfInstallments; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(createdLoan);
            installment.setAmount(BigDecimal.valueOf(totalAmount / numberOfInstallments));
            installment.setPaidAmount(BigDecimal.ZERO);
            installment.setDueDate(LocalDate.now().plusMonths(i + 1).withDayOfMonth(1));
            installment.setIsPaid(false);
            installmentService.saveInstallment(installment);
        }

        return ResponseEntity.ok("Loan created successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Loan>> listLoansById(@PathVariable Long id) {
        Optional<Loan> loan = loanService.findLoanById(id);
        return ResponseEntity.ok(loan);
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Loan>> listLoansByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(required = false) Boolean isPaid,
            @RequestParam(required = false) Integer numberOfInstallments) {
        List<Loan> loans = loanService.findLoansByCustomerId(customerId, isPaid, numberOfInstallments);
        return ResponseEntity.ok(loans);
    }
}
