package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "deliver")
public class Deliver {
    @Id
    @Column(name = "work_id") // 对应表中的work_id字段
    private String workId;  // 使用包装类型更安全

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    // 必须有无参构造函数
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

    public void setWorkId(String workId) {
        this.workId = workId;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setAge(Integer age){
        this.age = age;
    }
}
