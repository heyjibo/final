package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "informationofindent")
public class Indent {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "Content")
    private String content;

    @Column(name = "customerID")
    private String customerID;

    @Column(name = "Distance")
    private int distance;

    @Column(name = "Score")
    private int score;

    @Column(name = "deliverid")
    private String deliverID;

    @Column(name = "Money")
    private int money;

    @Column(name = "Time")
    private LocalDateTime time;

    @Column(name = "State")
    private String state;

    @Column(name = "completion_time")
    private LocalDateTime completionTime;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "pickup_time")
    private LocalDateTime pickupTime;

    @Column(name = "merchantID")
    private String merchantID;

    @Column(name = "deliver_Money")
    private int deliverMoney;

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }
    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }



    public Indent() {

    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getCustomerID() {
        return customerID;
    }
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public String getDeliverID() {
        return deliverID;
    }
    public void setDeliverID(String deliverID) {
        this.deliverID = deliverID;
    }

    public int getMoney() {
        return money;
    }
    public void setMoney(int money) {
        this.money = money;
    }

    public LocalDateTime getTime() {
        return time;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getPickupTime() {
        return pickupTime;
    }
    public void setPickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getMerchantID() { return merchantID; }
    public void setMerchantID(String merchantID) { this.merchantID = merchantID; }

    public int getDeliverMoney() {
        return deliverMoney;
    }
    public void setDeliverMoney(int deliverMoney) {
        this.deliverMoney = deliverMoney;
    }

}