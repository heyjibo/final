package com.example.demo.repository;

import com.example.demo.entity.Indent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndentRepository extends JpaRepository<Indent, String> {
    List<Indent> findByState(String state);
    List<Indent> findByDeliverID(String deliverID);
}
