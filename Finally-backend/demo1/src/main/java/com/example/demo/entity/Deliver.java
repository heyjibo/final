package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "deliver")
public class Deliver {
    @Id
    @Column(name = "work_id")
    private String workId;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "password")
    private String password;

    @Column(name = "numofindent")
    private Integer numofindent;

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

    public void setWorkId(String workId) {
        this.workId = workId;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setAge(Integer age){
        this.age = age;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setNumofindent(Integer numofindent){
        this.numofindent = numofindent;
    }
}
