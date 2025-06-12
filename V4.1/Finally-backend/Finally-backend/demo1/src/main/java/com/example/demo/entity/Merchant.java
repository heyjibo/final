package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "merchant")
public class Merchant {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "score")
    private double score;

    @Column(name = "numof_Indent")
    private int numOfIndent;

    @Column(name = "phonenumber")
    private String phonenumber;

    @Column(name = "isonline")
    private boolean isOnline;

    public Merchant() {}

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }

    public int getNumOfIndent() {
        return numOfIndent;
    }
    public void setNumOfIndent(int numOfIndent) {
        this.numOfIndent = numOfIndent;
    }

    public String getPhonenumber() {
        return phonenumber;
    }
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public boolean isOnline() {
        return isOnline;
    }
    public void setOnline(boolean online) {
        isOnline = online;
    }
}
