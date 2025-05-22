package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @Column(name = "customerid")
    private String customerid;

    @Column(name = "name")
    private String name;


    @Column(name = "address")
    private String address;

    @Column(name = "num_of_order")
    private Integer numOfOrder;

    @Column(name = "password")
    private String password;

    @Column(name = "phonenumber")
    private String phonenumber;

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

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhonenumber() {
        return phonenumber;
    }
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }


}
