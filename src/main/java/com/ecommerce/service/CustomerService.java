package com.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.entity.Customer;
import com.ecommerce.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    public void saveCustomer(Customer customer) {
        customerRepo.save(customer);
    }
}