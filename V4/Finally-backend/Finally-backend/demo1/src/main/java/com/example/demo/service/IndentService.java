package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.DeliverRepository;
import com.example.demo.repository.DishsRepository;
import com.example.demo.repository.IndentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class IndentService {
    private final IndentRepository indentRepository;
    private final DishsRepository dishsRepository;
    private final DeliverRepository deliverRepository;
    private final MerchantService merchantService;
    private final CustomerService customerService;

    @Autowired
    public IndentService(IndentRepository indentRepository, DishsRepository dishsRepository, DeliverRepository deliverRepository, MerchantService merchantService, CustomerService customerService) {
        this.indentRepository = indentRepository;
        this.dishsRepository = dishsRepository;
        this.deliverRepository = deliverRepository;
        this.merchantService = merchantService;
        this.customerService = customerService;
    }

    public List<Indent> translate(){
        List<Indent> indents = indentRepository.findAll();
        // 获取所有 Dishs 对象并存储到 List 中
        List<Dishs> dishsList = dishsRepository.findAll();

        for (Indent indent : indents) {
            String[] parts = indent.getContent().split(",");
            StringBuilder n = new StringBuilder();
            for (String part : parts) {
                // 遍历 dishsList 查找匹配的 Dishs 对象
                for (Dishs dish : dishsList) {
                    if (Integer.parseInt(dish.getID())==Integer.parseInt(part)) {
                        n.append(dish.getName()).append(",");
                        break; // 找到匹配项后退出内层循环
                    }
                }
            }
            // 去掉最后一个逗号
            if (n.length() > 0) {
                n.setLength(n.length() - 1);
            }
            indent.setContent(n.toString());
        }
        return indents;
    }


    public List<Indent> findByDeliveriD(String deliverID) {
        List<Indent> deliverOrders = indentRepository.findByDeliverID(deliverID);
        List<Indent> unassignedOrders = indentRepository.findUnassignedPendingOrders();

        List<Indent> result = new ArrayList<>();
        result.addAll(deliverOrders);
        result.addAll(unassignedOrders);

        return result;

        //return indentRepository.findByDeliverID(deliverID);
    }

    public List<Indent> getAllIndents() {
        return translate();
    }

    @Transactional
    public Indent getIndent(String id) {
        return indentRepository.findById(id).orElse(null);
    }

    public Indent saveIndent(Indent indent) {
        // 设置时间
        Random random = new Random();
        indent.setCreateTime(LocalDateTime.now());
        indent.setPickupTime(null);
        indent.setDistance( random.nextInt(20)+5);
        indent.setTime(null);
        for (Customer customer:customerService.getAllCustomers()) {
            if (customer.getCustomerID().equals(indent.getCustomerID())) {
                customer.setNumOfOrder(customer.getNumOfOrder() + 1);
            }
        }
        if (indent.getId() == null) {
            String maxId = indentRepository.findMaxId();
            int nextId = maxId == null ? 1 : Integer.parseInt(maxId) + 1;
            indent.setId(String.valueOf(nextId));
        }

        /*
        int x=0;
        for (Deliver deliver: deliverRepository.findAll()) {
            for (Indent indent1 : getAllIndents()) {
                if (indent1.getDeliverID().equals(deliver.getWorkId()) || indent1.getState().equals("Picking Up")||indent1.getState().equals("Delivered")) {
                    x=x+1;
                }
            }
            if (x<10){
                indent.setDeliverID(deliver.getWorkId());
                indent.setState("Awaits confirmation");
                deliver.setNumofindent(deliver.getNumofindent()+1);
                break;
            }
        }
        */
        indent.setState("Awaits confirmation");
        indent.setDeliverID(null);


        indent.setScore(0);
        List<Merchant> merchants = merchantService.getMerchants();
        for (Merchant merchant : merchants) {
            if (merchant.getId().equals(indent.getMerchantID())) {
                merchant.setNumOfIndent(merchant.getNumOfIndent()+1);
                merchantService.updateMerchant(merchant);
            }
        }


        List<Dishs> dishsList = dishsRepository.findAll();


        String[] parts = indent.getContent().split(",");
        StringBuilder n = new StringBuilder();
        for (String part : parts) {
            part = part.trim();
            for (Dishs dish : dishsList) {
                if (dish.getName().equals(part)) {
                    n.append(dish.getID()).append(",");
                    dish.setSalesVolume(dish.getSalesVolume() + 1); // 更新销售量
                    indent.setMoney(indent.getMoney() + dish.getUnitPrice());
                    break;
                }
            }
        }
        System.out.println(indent.getMoney());
        indent.setDeliverMoney(3);



        if (n.length() > 0) {
            n.setLength(n.length() - 1);
        }
        indent.setContent(n.toString());

        dishsList.forEach(dishsRepository::save);

        return indentRepository.save(indent);
    }

    public void deleteIndent(String id) {
        indentRepository.deleteById(id);
    }

    public List<Indent> getByMerchantID(String MerchantID) {
        List<Indent> result = new ArrayList<>();
        List<Indent> indents = translate();
        for (Indent indent : indents) {
            System.out.println(indent.getMerchantID());
            System.out.println(indent.getContent());
            if (MerchantID.equals(indent.getMerchantID())) {
                result.add(indent);
            }
        }
        return result;
    }

    public Indent updateScore(Indent indent) {
        if (indent.getId() == null) {
            throw new IllegalArgumentException("Indent ID must be set for update");
        }

        if (!indentRepository.existsById(indent.getId())) {
            throw new EntityNotFoundException("Indent with ID " + indent.getId() + " does not exist");
        }
        List<Merchant> merchants = merchantService.getMerchants();
        for (Merchant merchant : merchants) {
            if (merchant.getId().equals(indent.getMerchantID())) {
                merchant.setScore((merchant.getScore()* (merchant.getNumOfIndent()-1)+ indent.getScore())/merchant.getNumOfIndent());
                merchantService.updateMerchant(merchant);
            }
        }
        List<Deliver> delivers = deliverRepository.findAll();
        for (Deliver deliver : delivers) {
            if (deliver.getWorkId().equals(indent.getDeliverID())) {
                deliver.setScore((deliver.getScore()* (deliver.getNumofindent()-1)+ indent.getDeliverScore())/deliver.getNumofindent());
                deliverRepository.save(deliver);
            }
        }
        return indentRepository.save(indent);
    }

    public Indent updateIndent(Indent indent) {
        if (indent.getId() == null) {
            throw new IllegalArgumentException("Indent ID must be set for update");
        }

        if (!indentRepository.existsById(indent.getId())) {
            throw new EntityNotFoundException("Indent with ID " + indent.getId() + " does not exist");
        }
        return indentRepository.save(indent);
    }

    public List<Indent> searchByDate(LocalDate date) {
        List<Indent> result = new ArrayList<>();
        for (Indent indent : getAllIndents()) {
            if (date.isEqual(indent.getCreateTime().toLocalDate())) {
                result.add(indent);
            }
        }
        return result;
    }

    public int getTotalRevenue(String merchantId) {
        Integer sum = indentRepository.sumMoneyByMerchant(merchantId);
        return sum != null ? sum : 0;
    }

    public int getTotal(String deliverId) {
        Integer sum = indentRepository.sumMoneyByDeliver(deliverId);
        return sum != null ? sum : 0;
    }

    public List<Indent> findByCustomerIDAndState(String customerId, String state) {
        List<Indent> result =new ArrayList<>();
        for (Indent indent : getAllIndents()) {
            if (indent.getCustomerID().equals(customerId) && indent.getState().equals(state)) {
                result.add(indent);
            }
        }
        return result;
    }
}