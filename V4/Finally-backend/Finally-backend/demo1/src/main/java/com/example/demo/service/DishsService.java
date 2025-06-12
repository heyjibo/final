package com.example.demo.service;

import com.example.demo.entity.Dishs;
import com.example.demo.repository.DishsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishsService {
    private final DishsRepository dishsRepository;

    @Autowired
    public DishsService(DishsRepository dishsRepository) {
        this.dishsRepository = dishsRepository;
    }

    public List<Dishs> findAll() {
        return dishsRepository.findAll();
    }

    public Dishs findOne(String Id) {
        return dishsRepository.findById(Id).get();
    }

    public void save(Dishs dishs) {

        dishsRepository.save(dishs);
    }

    public void update(Dishs dishs) {
        // 1. 参数校验
        if (dishs.getID() == null || dishs.getID().isEmpty()) {
            throw new IllegalArgumentException("菜品ID不能为空");
        }

        // 2. 检查菜品是否存在
        if (!dishsRepository.existsById(dishs.getID())) {
            throw new EntityNotFoundException("未找到ID为 " + dishs.getID() + " 的菜品");
        }

        // 3. 直接更新（无需遍历）
        dishsRepository.save(dishs);
    }

    public List<Dishs> findByMerchantID(String merchantID) {
        return dishsRepository.findByMerchantID(merchantID);
    }
    public int countByMerchant(String merchantID) {
        return dishsRepository.countByMerchantID(merchantID);
    }

    public void delete(String ID) {
        dishsRepository.deleteById(ID);
    }
}
