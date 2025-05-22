package com.example.demo.controller;

import com.example.demo.entity.Deliver;
import com.example.demo.entity.Indent;
import com.example.demo.service.IndentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.util.JwtUtil;
import com.example.demo.repository.IndentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.example.demo.repository.DeliverRepository;




@RestController
@RequestMapping("api/indent")
@CrossOrigin
public class IndentController {
    private final IndentService indentService;
    private final IndentRepository indentRepository;

    @Autowired
    private DeliverRepository deliverRepository;

    @Autowired
    public IndentController(IndentService  indentService, IndentRepository indentRepository ) {
        this.indentRepository = indentRepository;
        this.indentService = indentService;
    }

    @GetMapping
    public List<Indent> getAll() {
        return indentService.getAllIndents();
    }

    @GetMapping("/getByMerchantID/{MerchantID}")
    public List<Indent> getByMerchantID(@PathVariable String MerchantID) {
        for (Indent indent : indentService.translate()) {
            System.out.println(indent.getContent());
        }
        return indentService.getByMerchantID(MerchantID);
    }

    @DeleteMapping("/{ID}")
    public void delete(@PathVariable String ID) {
        indentService.deleteIndent(ID);
    }

    @PutMapping("/creatNewIndent")
    public Indent createNewIndent(@RequestBody Indent indent) {
        return indentService.saveIndent(indent);
    }

    @PutMapping("/{ID}")
    public Indent update(@PathVariable String ID, @RequestBody Indent indent) {
        indent.setId(ID);
        return indentService.updateIndent(indent);
    }

    @PutMapping("/awaits/{ID}")
    public Indent updateAwaits(@PathVariable String ID) {
        Indent indent = indentService.getIndent(ID);
        indent.setTime(LocalDateTime.now());
        indent.setState("Pending");
        return indentService.updateIndent(indent);
    }

    @PutMapping("/pick/{ID}")
    public Indent updateState(
            @PathVariable String ID,
            @RequestBody Map<String, String> requestBody // 接收 JSON 为 Map
    ) {
        Indent indent = indentService.getIndent(ID);
        String workID = requestBody.get("workID"); // 从 Map 中提取 workID 值

        indent.setDeliverID(workID);
        indent.setPickupTime(LocalDateTime.now());
        indent.setState("Picking Up");
        return indentService.updateIndent(indent);
    }


    @PutMapping("/deliver/{ID}")
    public Indent updateDeliver(@PathVariable String ID) {
        Indent indent = indentService.getIndent(ID);
        indent.setCompletionTime(LocalDateTime.now());
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
        return indentService.findByDeliveriD(riderId);
    }

    @PutMapping("/score/{ID}")
    public Indent updateScore(@PathVariable String ID, @RequestBody int score) {
        Indent indent = indentService.getIndent(ID);
        indent.setScore(score);
        return indentService.updateScore(indent);
    }

    @GetMapping("/searchByDate/{date}")
    public List<Indent> searchByDate(@PathVariable LocalDateTime date) {
        return indentService.searchByDate(date);
    }

    @GetMapping("/searchByID/{ID}")
    public Indent searchByID(@PathVariable String ID) {
        return indentRepository.findById(ID).orElse(null);
    }

}