package com.example.demo.controller;

import com.example.demo.entity.Administrator;
import com.example.demo.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdministratorController {
    private final AdministratorService administratorService;

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

}
