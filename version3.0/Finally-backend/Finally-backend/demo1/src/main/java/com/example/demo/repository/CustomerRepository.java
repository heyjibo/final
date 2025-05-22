package com.example.demo.repository;

import com.example.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    List<Customer> findByName(String name);
    @Query("SELECT MAX(c.customerid) FROM Customer c")
    String findMaxCustomerId();

    boolean existsByName(String name);
    Customer findByPhonenumber(String phonenumber);
}
