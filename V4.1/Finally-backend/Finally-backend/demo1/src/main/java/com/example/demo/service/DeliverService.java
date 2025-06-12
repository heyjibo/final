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

    public List<Deliver> getAllDelivers() {
        return deliverRepository.findAll();
    }

    public Deliver getDeliverById(String workId) {
        return deliverRepository.findById(workId).orElse(null);
    }

    public Deliver saveDeliver(Deliver deliver) {
        // 自动生成工号（如果未提供）
        if (deliver.getWorkId() == null) {
            String maxWorkId = deliverRepository.findMaxWorkId();
            int nextId = maxWorkId == null ? 10000 : Integer.parseInt(maxWorkId) + 1;
            deliver.setWorkId(String.valueOf(nextId));
        }
        // 初始化接单数为0
        deliver.setNumofindent(0);
        return deliverRepository.save(deliver);
    }

    public void deleteDeliver(String workId) {
        deliverRepository.deleteById(workId);
    }

    // 根据 name 查询，如果查不到就报错
    public List<Deliver> getDeliverByName(String name) {
        List<Deliver> result = deliverRepository.findByName(name);
        if (result.isEmpty()) {
            throw new RuntimeException("未找到名称为 '" + name + "' 的记录！");
        }
        return result;
    }

    public int countAvailableRiders() {
        return deliverRepository.countAvailableRiders();
    }
}
