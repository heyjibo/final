package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Dishs {
    @Id
    @Column(name = "id")
    @JsonProperty("id")
    private String ID;

    @Column(name = "merchantid")
    private String merchantID;

    @Column(name = "name")
    private String name;

    @Column(name = "unit-price")
    private int unitPrice;

    @Column(name = "sales-volume")
    private int salesVolume;

    public Dishs(){}

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMerchantID() {
        return merchantID;
    }
    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getSalesVolume() {
        return salesVolume;
    }
    public void setSalesVolume(int salesVolume) {
        this.salesVolume = salesVolume;
    }
}