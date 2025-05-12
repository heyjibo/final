package com.example.demo.controller;

import com.example.demo.entity.Deliver;
import com.example.demo.entity.DeliverTime;
import com.example.demo.entity.Indent;
import com.example.demo.service.IndentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.util.JwtUtil;
import com.example.demo.repository.IndentRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.example.demo.repository.DeliverRepository;




@RestController
@RequestMapping("api/indent")
@CrossOrigin
public class IndentController {
    private final IndentService indentService;

    @Autowired
    private DeliverRepository deliverRepository;
    @Autowired
    private IndentRepository indentRepository;

    @Autowired
    public IndentController(IndentService indentService) {
        this.indentService = indentService;
    }

    @GetMapping
    public List<Indent> getAll() {
        return indentService.getAllIndents();
    }

    @GetMapping("/{ID}")
    public Indent get(@PathVariable String ID) {
        return indentService.getIndent(ID);
    }

    @DeleteMapping("/{ID}")
    public void delete(@PathVariable String ID) {
        indentService.deleteIndent(ID);
    }

    @PutMapping("/{ID}")
    public Indent update(@PathVariable String ID, @RequestBody Indent indent) {
        indent.setId(ID);
        return indentService.updateIndent(indent);
    }

    @PutMapping("/pick/{ID}")
    public Indent updateState(@PathVariable String ID) {
        Indent indent = indentService.getIndent(ID);
        indent.setState("Picking Up");
        return indentService.updateIndent(indent);
    }


    @PutMapping("/deliver/{ID}")
    public Indent updateDeliver(@PathVariable String ID) {
        Indent indent = indentService.getIndent(ID);
        indent.setState("Delivering");
        return indentService.updateIndent(indent);
    }

    @Transactional
    @PutMapping("/complete/{ID}")
    public Indent updateComplete(@PathVariable String ID) {
        Indent indent = indentService.getIndent(ID);
        System.out.println("[DEBUG] 订单ID: " + ID + ", DeliverID: " + indent.getDeliverID());

        indent.setState("Completed");
        indent.setCompletionTime(LocalDateTime.now());

        Deliver deliver = deliverRepository.findById(indent.getDeliverID()).orElse(null);
        if(deliver != null){
            int currentNum = deliver.getNumofindent();
            deliver.setNumofindent(deliver.getNumofindent() + 1);
            deliverRepository.save(deliver);
            deliverRepository.flush();
            System.out.println("[DEBUG] 骑手数据已刷新到数据库");
            System.out.println("[DEBUG] 骑手工号: " + deliver.getWorkId() +
                    ", 原接单数: " + currentNum  +
                    ", 新接单数: " + deliver.getNumofindent());
        }else {
            System.out.println("[ERROR] 未找到关联的骑手: " + indent.getDeliverID());
        }
        return indentService.updateIndent(indent);
    }

    @PutMapping("/change-deliver/{ID}")
    public Indent changeDeliver(@PathVariable String ID, @RequestBody Map<String, String> request) {
        Indent indent = indentService.getIndent(ID);
        indent.setDeliverID(request.get("deliverID"));
        return indentService.updateIndent(indent);
    }
    @GetMapping("/my-orders")
    public List<Indent> getMyOrders(@RequestHeader("Authorization") String token) {
        String riderId = JwtUtil.parseToken(token.replace("Bearer ", ""));
        return indentRepository.findByDeliverID(riderId);
    }

    @GetMapping("/time/{ID}")
    public ResponseEntity<DeliverTime> delivertime(@PathVariable String ID) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration;
        DeliverTime deliverTime = new DeliverTime();
        int state = 0;

        Indent indent = indentService.getIndent(ID);
        if (indent == null) {
            return ResponseEntity.notFound().build();
        }

        String stateStr = indent.getState();
        LocalDateTime indentTime = indent.getTime();

        duration = Duration.between(now, indentTime);

        if ("Completed".equals(stateStr)) {
            state = 1;
        } else if ("Delivering".equals(stateStr)) {
            state = now.isBefore(indentTime) ? 2 : 0;
        }

        deliverTime.setTime(duration);
        deliverTime.setState(state);

        return ResponseEntity.ok(deliverTime);
    }
}