package com.example.demo.controller;


import com.example.demo.entity.Indent;
import com.example.demo.entity.Merchant;
import com.example.demo.repository.IndentRepository;
import com.example.demo.repository.MerchantRespository;
import com.example.demo.service.DeliverService;
import com.example.demo.service.DishsService;
import com.example.demo.service.IndentService;
import com.example.demo.service.MerchantService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.tokens.Token;

import java.util.*;

@RestController
@RequestMapping("/api/merchant")
@CrossOrigin
public class MerchantController {
    private final MerchantService merchantService;
    private final MerchantRespository merchantRespository;
    private final IndentService indentService;
    private final DishsService dishsService;
    private final DeliverService deliverService;
    private final IndentRepository indentRepository;

    @Autowired
    public MerchantController(
            MerchantRespository merchantRespository,
            MerchantService merchantService,
            IndentService indentService,
            DishsService dishsService,
            DeliverService deliverService,
            IndentRepository indentRepository
    ) {
        this.merchantRespository = merchantRespository;
        this.merchantService = merchantService;
        this.indentService = indentService;
        this.dishsService = dishsService;
        this.deliverService = deliverService;
        this.indentRepository = indentRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getAll(){
        List<Merchant> merchants = merchantService.getMerchants();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Merchant merchant : merchants) {
            Map<String, Object> merchantMap = new HashMap<>();
            merchantMap.put("id", merchant.getId());
            merchantMap.put("name", merchant.getName());
            merchantMap.put("score", merchant.getScore());
            merchantMap.put("numOfIndent", merchant.getNumOfIndent());
            merchantMap.put("phonenumber", merchant.getPhonenumber());
            merchantMap.put("isOnline", merchant.isOnline()); // 添加isOnline字段
            result.add(merchantMap);
        }
        return result;
    }

    @GetMapping("/{ID}")
    public Merchant get(@PathVariable String ID){
        return merchantService.getMerchantByID(ID);
    }

    @PostMapping("/login")
    public ResponseEntity<String> merchantLogin(@RequestBody Merchant merchant) {
        // 打印接收到的请求体
        System.out.println("[DEBUG] 登录请求数据: " + merchant);

        // 检查必要字段是否存在
        if (merchant.getId() == null || merchant.getPassword() == null) {
            System.out.println("[ERROR] 请求缺少 workId 或 password");
            return ResponseEntity.badRequest().body("缺失工号或密码");
        }

        String loginInput = merchant.getId();

        // 查询数据库
        Merchant existing = merchantRespository.findById(merchant.getId()).orElse(null);
        if (existing == null) {
            if (merchantRespository.findByPhonenumber(loginInput) == null) {
                System.out.println("[ERROR] 用户不存在: " + loginInput);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登录凭证或密码错误");
            } else {
                existing = merchantRespository.findByPhonenumber(loginInput);
            }
        }

        // 打印密码比对信息
        System.out.println("[DEBUG] 数据库密码: " + existing.getPassword());
        System.out.println("[DEBUG] 输入密码: " + merchant.getPassword());

        // 验证密码
        if (!existing.getPassword().equals(merchant.getPassword())) {
            System.out.println("[ERROR] 密码不匹配");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("工号或密码错误");
        }

        // 生成 Token
        String token = JwtUtil.generateToken(existing.getId());
        System.out.println("[DEBUG] 登录成功，生成 Token: " + token);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerMerchant(@RequestBody Merchant merchant) {
        // 1. 检查必填字段（修正重复的name检查）
        if (merchant.getName() == null || merchant.getPassword() == null || merchant.getPhonenumber() == null) { // 移除多余的第三个null检查
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "缺失必要字段（姓名、密码）") // 移除"年龄"提示
            );
        }
        // 2. 检查用户名重复
        if (merchantRespository.existsByName(merchant.getName())) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "用户名已被占用")
            );
        }
        try {
            // 3. 保存并生成工号
            merchant = merchantService.saveMerchant(merchant);
            // 4. 构建返回结果
            Map<String, String> response = new HashMap<>();
            response.put("workId", merchant.getId());
            response.put("password", merchant.getPassword());
            response.put("message", "注册成功，请使用工号登录");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Collections.singletonMap("error", "系统错误：" + e.getMessage())
            );
        }
    }

    @GetMapping("/stats")
    public Map<String, Object> getMerchantStats(@RequestHeader("Authorization") String token) {
        String merchantId = JwtUtil.parseToken(token.replace("Bearer ", ""));

        System.out.println("[DEBUG] 当前商家ID: " + merchantId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("dishCount", dishsService.countByMerchant(merchantId));
        stats.put("riderCount", deliverService.countAvailableRiders());
        stats.put("totalRevenue", indentService.getTotalRevenue(merchantId));

        return stats;
    }

    @GetMapping("/orders")
    public List<Indent> getMerchantOrders(@RequestHeader("Authorization") String token) {
        String merchantId = JwtUtil.parseToken(token.replace("Bearer ", ""));
        return indentRepository.findByMerchantID(merchantId);
    }

    @GetMapping("/customer/completed/{customerId}")
    public List<Indent> getCompletedOrdersByCustomer(@PathVariable String customerId) {
        return indentService.findByCustomerIDAndState(customerId, "Completed");
    }

    @GetMapping("/phonenumber/{ID}")
    public String getMerchantPhonenumber(@PathVariable String ID) {
        return merchantService.getMerchantByID(ID).getPhonenumber();
    }

    @PutMapping("/Online/{ID}")
    public Merchant updateOnlineByID(@PathVariable String ID){
        Merchant merchant = merchantService.getMerchantByID(ID);
        merchant.setOnline(true);
        return merchantService.updateMerchant(merchant);
    }

    @PutMapping("/Outline/{ID}")
    public Merchant updateOutlineByID(@PathVariable String ID){
        Merchant merchant = merchantService.getMerchantByID(ID);
        merchant.setOnline(false);
        return merchantService.updateMerchant(merchant);
    }

    @GetMapping("/isOnline")
    public List<Merchant> searchByisOnline(){
        List<Merchant> result = new ArrayList<>();
        for (Merchant merchant : merchantService.getMerchants()) {
            if (merchant.isOnline()) {
                result.add(merchant);
            }
        }
        return result;
    }

}
