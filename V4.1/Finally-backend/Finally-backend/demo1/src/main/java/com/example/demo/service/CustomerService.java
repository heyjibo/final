package com.example.demo.service;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(String customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    public Customer saveCustomer(Customer customer) {
        // 自动生成工号（如果未提供）
        if (customer.getCustomerID() == null) {
            String maxWorkId = customerRepository.findMaxCustomerId();
            int nextId = maxWorkId == null ? 100000 : Integer.parseInt(maxWorkId) + 1;
            customer.setCustomerID(String.valueOf(nextId));
        }
        customer.setNumOfOrder(0);
        return customerRepository.save(customer);
    }

    public void deleteCustomer(String customerId) {
        customerRepository.deleteById(customerId);
    }


}
