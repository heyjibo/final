package com.example.demo.repository;

import com.example.demo.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MerchantRespository extends JpaRepository<Merchant, String> {
    List<Merchant> findByName(String name);
    @Query("SELECT MAX(m.id) FROM Merchant m")
    String findMaxWorkId();
    boolean existsByName(String name);
    Merchant findByPhonenumber(String phonenumber);
}
