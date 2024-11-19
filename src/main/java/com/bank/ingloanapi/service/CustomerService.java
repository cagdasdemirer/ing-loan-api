package com.bank.ingloanapi.service;

import com.bank.ingloanapi.model.Customer;
import com.bank.ingloanapi.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public boolean hasEnoughCreditLimit(Customer customer, double loanAmount) {
        double availableLimit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit()).doubleValue();
        return loanAmount <= availableLimit;
    }

    public void updateUsedCreditLimit(Customer customer, double loanAmount) {
        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(BigDecimal.valueOf(loanAmount)));
        customerRepository.save(customer);
    }
}
