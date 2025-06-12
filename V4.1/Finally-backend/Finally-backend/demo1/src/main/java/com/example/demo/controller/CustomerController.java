package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.CustomerService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerController(CustomerService customerService, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public List<Customer> getAll() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{customerId}")
    public Customer get(@PathVariable String customerId) {
        return customerService.getCustomerById(customerId);
    }

    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }

    @PutMapping
    public Customer update(@RequestBody String customerId, @RequestBody Customer customer) {
        customer.setCustomerID(customerId);
        return customerService.saveCustomer(customer);
    }

    @DeleteMapping
    public void delete(@RequestBody String customerId) {
        customerService.deleteCustomer(customerId);
    }

    @PostMapping("/login")
    public ResponseEntity<String> customerLogin(@RequestBody Customer customer) {
        // 打印接收到的请求体
        System.out.println("[DEBUG] 登录请求数据: " + customer);

        // 检查必要字段是否存在
        if (customer.getCustomerID() == null || customer.getPassword() == null) {
            System.out.println("[ERROR] 请求缺少登录标识或密码");
            return ResponseEntity.badRequest().body("缺失登录凭证或密码");
        }

        String loginInput = customer.getCustomerID();
        Customer existing = null;

        // 优先尝试工号查询
        existing = customerRepository.findById(loginInput).orElse(null);

        // 工号不存在时尝试手机号查询
        if (existing == null) {
            System.out.println("[DEBUG] 尝试手机号登录: " + loginInput);
            existing = customerRepository.findByPhonenumber(loginInput);
        }

        // 用户不存在处理
        if (existing == null) {
            System.out.println("[ERROR] 用户不存在: " + loginInput);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登录凭证或密码错误");
        }

        // 密码验证
        System.out.println("[DEBUG] 数据库密码: " + existing.getPassword());
        System.out.println("[DEBUG] 输入密码: " + customer.getPassword());

        if (!existing.getPassword().equals(customer.getPassword())) {
            System.out.println("[ERROR] 密码不匹配");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登录凭证或密码错误");
        }

        // 生成 Token
        String token = JwtUtil.generateToken(existing.getCustomerID());
        System.out.println("[DEBUG] 登录成功，生成 Token: " + token);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerDeliver(@RequestBody Customer customer) {
        // 1. 检查必填字段
        if (customer.getName() == null || customer.getPassword() == null || customer.getAddress() == null || customer.getPhonenumber() == null) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "缺失必要字段（姓名、密码、年龄）")
            );
        }
        // 2. 检查用户名重复
        if (customerRepository.existsByName(customer.getName())) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "用户名已被占用")
            );
        }
        try {
            // 3. 保存并生成工号
            customer = customerService.saveCustomer(customer);
            // 4. 构建返回结果
            Map<String, String> response = new HashMap<>();
            response.put("workId", customer.getCustomerID());
            response.put("password", customer.getPassword());
            response.put("message", "注册成功，请使用工号登录");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Collections.singletonMap("error", "系统错误：" + e.getMessage())
            );
        }
    }

    @GetMapping("/phonenumber/{ID}")
    public String getCustomerPhonenumber(@PathVariable String ID) {
        return customerService.getCustomerById(ID).getPhonenumber();
    }
}