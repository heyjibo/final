package com.example.demo.repository;
import java.util.List;
import com.example.demo.entity.Dishs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DishsRepository extends JpaRepository<Dishs, String> {
    List<Dishs> findByMerchantID(String merchantID);
    @Query("SELECT MAX(d.ID) FROM Dishs d")
    String findMaxId();
    @Query("SELECT COUNT(d) FROM Dishs d WHERE d.ID = :ID")
    int countByMerchantID(@Param("ID") String ID);
}
