package com.bank.ingloanapi.repository;

import com.bank.ingloanapi.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
