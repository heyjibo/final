package com.example.demo.repository;

import com.example.demo.entity.Indent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IndentRepository extends JpaRepository<Indent, String> {
    List<Indent> findByState(String state);
    List<Indent> findByDeliverID(String deliverID);
    List<Indent> findByMerchantID(String merchantID);
    @Query("SELECT MAX(i.id) FROM Indent i")
    String findMaxId();
    @Query("SELECT SUM(i.money) FROM Indent i WHERE i.merchantID = :merchantId")
    Integer sumMoneyByMerchant(@Param("merchantId") String merchantId);
    @Query("SELECT SUM(i.deliverMoney) FROM Indent i WHERE i.deliverID = :deliverID")
    Integer sumMoneyByDeliver(@Param("deliverID") String deliverID);
    @Query("SELECT i FROM Indent i WHERE i.deliverID IS NULL AND i.state = 'Pending'")
    List<Indent> findUnassignedPendingOrders();
}
