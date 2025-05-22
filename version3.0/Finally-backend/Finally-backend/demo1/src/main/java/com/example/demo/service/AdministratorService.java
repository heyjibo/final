package com.example.demo.service;

import com.example.demo.entity.Administrator;
import com.example.demo.entity.Deliver;
import com.example.demo.entity.Indent;
import com.example.demo.repository.AdministratorRepository;
import com.example.demo.repository.DeliverRepository;
import com.example.demo.repository.IndentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdministratorService {
    private final AdministratorRepository administratorRepository;

    @Autowired
    private DeliverRepository deliverRepository;

    @Autowired
    private IndentRepository indentRepository;

    @Autowired
    public AdministratorService(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    public List<Administrator> getAllAdministrators() {
        return administratorRepository.findAll();
    }

    public Administrator saveAdministrator(Administrator administrator) {
        return administratorRepository.save(administrator);
    }

    public boolean checkPassword(String adminId, String password) {
        Administrator admin = administratorRepository.findById(adminId).orElse(null);
        return admin != null && admin.getPassword().equals(password);
    }

    public Map<String, Object> getRidersOverview() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有骑手
        List<Deliver> riders = deliverRepository.findAll();

        // 获取所有已完成订单
        List<Indent> completedOrders = indentRepository.findByState("Completed");

        // 计算统计数据
        int totalRiders = riders.size();
        int totalCompleted = completedOrders.size();
        double totalRevenue = completedOrders.stream().mapToDouble(Indent::getMoney).sum();

        // 构建骑手详情
        List<Map<String, Object>> riderDetails = riders.stream().map(rider -> {
            Map<String, Object> detail = new HashMap<>();
            List<Indent> riderOrders = completedOrders.stream()
                    .filter(o -> o.getDeliverID().equals(rider.getWorkId()))
                    .toList();

            detail.put("workId", rider.getWorkId());
            detail.put("name", rider.getName());
            detail.put("age", rider.getAge());
            detail.put("numofindent", riderOrders.size());
            detail.put("totalRevenue", riderOrders.stream().mapToDouble(Indent::getMoney).sum());
            detail.put("lastActive", riderOrders.stream()
                    .map(Indent::getTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null));

            return detail;
        }).collect(Collectors.toList());

        result.put("totalRiders", totalRiders);
        result.put("totalCompletedOrders", totalCompleted);
        result.put("totalRevenue", totalRevenue);
        result.put("riders", riderDetails);

        completedOrders.forEach(o ->
                log.info("订单 deliverID: {}", o.getDeliverID())
        );

        // 打印骑手工号
        riders.forEach(rider ->
                log.info("骑手工号: {}", rider.getWorkId())
        );

        return result;
    }

}