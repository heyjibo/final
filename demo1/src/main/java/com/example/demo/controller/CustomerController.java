package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getAll() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{customerId}")
    public Customer get(@PathVariable String customerId) {
        return customerService.getCustomerById(customerId);
    }

    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }

    @PutMapping
    public Customer update(@PathVariable String customerId, @RequestBody Customer customer) {
        customer.setCustomerID(customerId);
        return customerService.saveCustomer(customer);
    }

    @DeleteMapping
    public void delete(@PathVariable String customerId) {
        customerService.deleteCustomer(customerId);
    }
}
