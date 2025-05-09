package com.example.demo.controller;

import com.example.demo.entity.DeliverTime;
import com.example.demo.entity.Indent;
import com.example.demo.service.IndentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/indent")
@CrossOrigin
public class IndentController {
    private final IndentService indentService;

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
    public Indent updateState(@PathVariable String ID, @RequestBody Indent indent) {
        indent.setState("Picking Up");
        return indentService.updateIndent(indent);
    }

    @PutMapping("/deliver/{ID}")
    public Indent updateDeliver(@PathVariable String ID, @RequestBody Indent indent) {
        indent.setState("Delivering");
        return indentService.updateIndent(indent);
    }

    @PutMapping("/complete/{ID}")
    public Indent updateComplete(@PathVariable String ID, @RequestBody Indent indent) {
        indent.setState("Completed");
        return indentService.updateIndent(indent);
    }

    @GetMapping("/time/{ID}")
    public ResponseEntity<DeliverTime> delivertime(@PathVariable String ID) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = null;
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
