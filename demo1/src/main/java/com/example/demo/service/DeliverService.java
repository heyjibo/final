package com.example.demo.service;

import com.example.demo.entity.Deliver;
import com.example.demo.repository.DeliverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliverService {
    private final DeliverRepository deliverRepository;

    @Autowired
    public DeliverService(DeliverRepository deliverRepository) {
        this.deliverRepository = deliverRepository;
    }

    // 获取所有数据
    public List<Deliver> getAllDelivers() {
        return deliverRepository.findAll();
    }

    // 根据work_id查询
    public Deliver getDeliverById(String workId) {
        return deliverRepository.findById(workId).orElse(null);
    }

    // 新增或修改数据
    public Deliver saveDeliver(Deliver deliver) {
        return deliverRepository.save(deliver);
    }

    // 删除数据
    public void deleteDeliver(String workId) {
        deliverRepository.deleteById(workId);
    }
}
