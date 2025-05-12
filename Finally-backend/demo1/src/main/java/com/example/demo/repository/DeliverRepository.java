package com.example.demo.repository;

import com.example.demo.entity.Deliver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;



public interface DeliverRepository extends JpaRepository<Deliver, String> {

    // 根据 name 查找 Deliver 列表
    List<Deliver> findByName(String name);

    @Query("SELECT MAX(d.workId) FROM Deliver d")
    String findMaxWorkId();

    boolean existsByName(String name);
}
