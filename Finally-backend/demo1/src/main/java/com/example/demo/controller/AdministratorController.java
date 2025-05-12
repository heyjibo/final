package com.example.demo.controller;

import com.example.demo.entity.Administrator;
import com.example.demo.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repository.AdministratorRepository;
import java.util.Map;
import com.example.demo.util.JwtUtil;
import java.util.List;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdministratorController {
    private final AdministratorService administratorService;


    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @GetMapping
    public List<Administrator> getAll() {
        return administratorService.getAllAdministrators();
    }

    @PostMapping
    public Administrator create(@RequestParam Administrator administrator) {
        return administratorService.saveAdministrator(administrator);
    }

    @PutMapping("/{ID}")
    public Administrator update(@PathVariable String ID, @RequestBody Administrator administrator) {
        administrator.setId(ID);
        return administratorService.saveAdministrator(administrator);
    }

    @PostMapping("/checkpassword")
    public ResponseEntity<Boolean> checkPassword(@RequestBody Administrator administrator) {
        List<Administrator> administrators = administratorService.getAllAdministrators();
        for (Administrator admin : administrators) {
            if (admin.getId().equals(administrator.getId()) && admin.getPassword().equals(administrator.getPassword())) {
                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.ok(false);
    }

    @PostMapping("/login")
    public ResponseEntity<String> adminLogin(@RequestBody Administrator admin) {
        Administrator existing = administratorRepository.findById(admin.getId()).orElse(null);
        if (existing == null || !existing.getPassword().equals(admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        String token = JwtUtil.generateToken(existing.getId());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Administrator> registerAdmin(@RequestBody Administrator admin) {
        if (administratorRepository.existsById(admin.getId())) {
            throw new RuntimeException("管理员已存在！");
        }
        return ResponseEntity.ok(administratorService.saveAdministrator(admin));
    }

    @GetMapping("/riders")
    public ResponseEntity<Map<String, Object>> getRidersOverview() {
        Map<String, Object> result = administratorService.getRidersOverview();
        return ResponseEntity.ok(result);
    }

}

