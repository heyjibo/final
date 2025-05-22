package com.example.demo.controller;

import com.example.demo.entity.Dishs;
import com.example.demo.repository.DishsRepository;
import com.example.demo.service.DishsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dish")
@CrossOrigin
public class DishsController {
    private final DishsRepository dishsRepository;
    private final DishsService dishsService;

    @Autowired
    public DishsController(DishsRepository dishsRepository, DishsService dishsService) {
        this.dishsRepository = dishsRepository;
        this.dishsService = dishsService;
    }

    @GetMapping
    public List<Dishs> getAll(){
        return dishsRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dishs> getDishById(@PathVariable String id) {
        Optional<Dishs> dish = dishsRepository.findById(id);
        return dish.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/createNewDish")  // 注意修正拼写 creat -> create
    public void createNewDish(@RequestBody Dishs dishs) {
        if (dishs.getID() == null) {
            String maxId = dishsRepository.findMaxId();
            int nextId = maxId == null ? 1 : Integer.parseInt(maxId) + 1;
            dishs.setID(String.valueOf(nextId));
        }
        dishs.setSalesVolume(0);
        dishsService.save(dishs);
    }

    @GetMapping("/getByMerchant/{merchantID}")
    public List<Dishs> getByMerchant(@PathVariable("merchantID") String merchantID) {
        return dishsService.findByMerchantID(merchantID);
    }
    // 更新菜品
    @PutMapping("/update")
    public ResponseEntity<?> updateDish(@RequestBody Dishs dishs) {
        try {
            // 1. 校验必要字段
            if (dishs.getID() == null || dishs.getID().isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "菜品ID不能为空"));
            }

            // 2. 检查菜品是否存在
            if (!dishsRepository.existsById(dishs.getID())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "菜品不存在，ID: " + dishs.getID()));
            }

            // 3. 执行更新
            dishsService.update(dishs);
            return ResponseEntity.ok(Collections.singletonMap("message", "菜品更新成功"));

        } catch (Exception e) {
            System.err.println("更新菜品失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "服务器错误: " + e.getMessage()));
        }
    }

    // 删除菜品
    @DeleteMapping("/delete/{ID}")
    public void deleteDish(@PathVariable String ID) {
        // 检查菜品是否存在
        if (!dishsRepository.existsById(ID)) {
            throw new RuntimeException("菜品不存在，无法删除！");
        }
        dishsService.delete(ID);
    }
}