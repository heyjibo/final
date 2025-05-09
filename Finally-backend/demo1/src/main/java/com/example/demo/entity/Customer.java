package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @Column(name = "customerid")
    private String customerid;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "address")
    private String address;

    @Column(name = "numOfOrder")
    private Integer numOfOrder;

    public Customer() {}

    public String getCustomerID() {
        return customerid;
    }
    public void setCustomerID(String customerId) {
        this.customerid = customerId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getNumOfOrder() {
        return numOfOrder;
    }
    public void setNumOfOrder(Integer numOfOrder) {
        this.numOfOrder = numOfOrder;
    }


}
