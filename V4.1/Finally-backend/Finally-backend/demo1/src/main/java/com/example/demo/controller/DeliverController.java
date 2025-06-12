package com.example.demo.controller;

import com.example.demo.entity.Deliver;
import com.example.demo.service.DeliverService;
import com.example.demo.service.IndentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repository.DeliverRepository;
import com.example.demo.util.JwtUtil;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/deliver")
@CrossOrigin
public class DeliverController {
    private final DeliverService deliverService;
    private final DeliverRepository deliverRepository;
    private final IndentService indentService;

    @Autowired
    public DeliverController(DeliverService deliverService, DeliverRepository deliverRepository, IndentService indentService) {
        this.deliverService = deliverService;
        this.deliverRepository = deliverRepository;
        this.indentService = indentService;
    }

    // 获取全部
    @GetMapping
    public List<Deliver> getAll() {
        return deliverService.getAllDelivers();
    }

    // 根据 workId 获取
    @GetMapping("/{workId}")
    public Deliver getById(@PathVariable String workId) {
        return deliverService.getDeliverById(workId);
    }

    // ✅ 根据 name 查询（如果没有就报错）
    @GetMapping("/search")
    public List<Deliver> getByName(@RequestParam String name) {
        return deliverService.getDeliverByName(name);
    }

    // 创建
    @PostMapping
    public Deliver create(@RequestBody Deliver deliver) {
        return deliverService.saveDeliver(deliver);
    }

    // 修改（修复路径中未写 workID）
    @PutMapping("/{workID}")
    public Deliver update(@PathVariable String workID, @RequestBody Deliver deliver) {
        deliver.setWorkId(workID);
        return deliverService.saveDeliver(deliver);
    }

    // 删除（修复路径中未写 workID）
    @DeleteMapping("/{workID}")
    public void delete(@PathVariable String workID) {
        deliverService.deleteDeliver(workID);
    }

    @GetMapping("/deliverMoney/{deliverID}")
    public int getTotal(@PathVariable String deliverID) {
        return indentService.getTotal(deliverID);
    }

    //根据单量排序
    @GetMapping("/delivers")
    public ResponseEntity<List<Deliver>> getSortedDelivers() {
        List<Deliver> allDelivers = getAll();
        allDelivers.sort((d1, d2) -> Integer.compare(d2.getNumofindent(), d1.getNumofindent()));
        return ResponseEntity.ok(allDelivers);
    }
    @GetMapping("/profile")
    public ResponseEntity<Deliver> getRiderProfile(@RequestHeader("Authorization") String token) {
        String workId = JwtUtil.parseToken(token); // 需要实现JWT解析逻辑
        Deliver deliver = deliverService.getDeliverById(workId);
        return ResponseEntity.ok(deliver);
    }

    @PostMapping("/login")
    public ResponseEntity<String> deliverLogin(@RequestBody Deliver deliver) {
        // 打印接收到的请求体
        System.out.println("[DEBUG] 登录请求数据: " + deliver);

        // 检查必要字段是否存在
        if (deliver.getWorkId() == null || deliver.getPassword() == null) {
            System.out.println("[ERROR] 请求缺少 workId 或 password");
            return ResponseEntity.badRequest().body("缺失工号或密码");
        }

        String loginInput = deliver.getWorkId();

        // 查询数据库
        Deliver existing = deliverRepository.findById(deliver.getWorkId()).orElse(null);
        if (existing == null) {
            if (deliverRepository.findByPhonenumber(loginInput) == null) {
                System.out.println("[ERROR] 用户不存在: " + loginInput);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登录凭证或密码错误");
            } else {
                existing = deliverRepository.findByPhonenumber(loginInput);
            }

        }

        // 打印密码比对信息
        System.out.println("[DEBUG] 数据库密码: " + existing.getPassword());
        System.out.println("[DEBUG] 输入密码: " + deliver.getPassword());

        // 验证密码
        if (!existing.getPassword().equals(deliver.getPassword())) {
            System.out.println("[ERROR] 密码不匹配");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("工号或密码错误");
        }

        // 生成 Token
        String token = JwtUtil.generateToken(existing.getWorkId());
        System.out.println("[DEBUG] 登录成功，生成 Token: " + token);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerDeliver(@RequestBody Deliver deliver) {
        // 1. 检查必填字段
        if (deliver.getName() == null || deliver.getPassword() == null || deliver.getAge() == null || deliver.getPhonenumber() == null) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "缺失必要字段（姓名、密码、年龄）")
            );
        }
        // 2. 检查用户名重复
        if (deliverRepository.existsByName(deliver.getName())) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "用户名已被占用")
            );
        }
        try {
            deliver.setNumofindent(0);
            // 3. 保存并生成工号
            deliver = deliverService.saveDeliver(deliver);
            // 4. 构建返回结果
            Map<String, String> response = new HashMap<>();
            response.put("workId", deliver.getWorkId());
            response.put("password", deliver.getPassword());
            response.put("message", "注册成功，请使用工号登录");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Collections.singletonMap("error", "系统错误：" + e.getMessage())
            );
        }
    }

    @GetMapping("/phonenumber/{ID}")
    public String getDeliverPhonenumber(@PathVariable String ID) {
        return deliverService.getDeliverById(ID).getPhonenumber();
    }
}