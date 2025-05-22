package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "deliver")
public class Deliver {
    @Id
    @Column(name = "work_id")
    @JsonProperty("workId")
    private String workId;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "password")
    private String password;

    @Column(name = "numofindent")
    private Integer numofindent;

    @Column(name = "phonenumber")
    private String phonenumber;

    public Deliver() {
    }


    public String getWorkId() {
        return workId;
    }
    public String getName() {
        return name;
    }
    public Integer getAge() {
        return age;
    }
    public String getPassword() {
        return password;
    }
    public Integer getNumofindent() {
        return numofindent;
    }
    public String getPhonenumber() {
        return phonenumber;
    }


    public void setWorkId(String workId) {
        this.workId = workId;
    }
    public void setName(String name){this.name = name;}
    public void setAge(Integer age){
        this.age = age;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setNumofindent(Integer numofindent){
        this.numofindent = numofindent;
    }
    public void setPhonenumber(String phonenumber){
        this.phonenumber = phonenumber;
    }
}
