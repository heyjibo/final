package com.example.demo.controller;

import com.example.demo.entity.Deliver;
import com.example.demo.service.DeliverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliver")
@CrossOrigin
public class DeliverController {
    private final DeliverService deliverService;

    @Autowired
    public DeliverController(DeliverService deliverService) {
        this.deliverService = deliverService;
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
}
