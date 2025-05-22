package com.example.demo.controller;

import com.example.demo.service.DataBaseBackUpService;
import com.example.demo.service.DataBaseRestoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/database")
public class DataBaseController {
    private final DataBaseBackUpService dataBaseBackUpService;
    private final DataBaseRestoreService dataBaseRestoreService;

    @Autowired
    public DataBaseController(DataBaseBackUpService dataBaseBackUpService, DataBaseRestoreService dataBaseRestoreService) {
        this.dataBaseBackUpService = dataBaseBackUpService;
        this.dataBaseRestoreService = dataBaseRestoreService;
    }

    @PostMapping("/backup")
    public String backupDatabase(@RequestParam String username, @RequestParam String password, @RequestParam String databaseName, @RequestParam String backupFileName) {
        try {
            dataBaseBackUpService.backupDatabase(username, password, databaseName, backupFileName);
            return "备份成功";
        } catch (Exception e) {
            return "备份失败: " + e.getMessage();
        }
    }

    @PostMapping("/restore")
    public String restoreDatabase(String username, String password, String databaseName, String backupFilePath) throws IOException, InterruptedException {
        try {
            dataBaseRestoreService.restoreDatabase(username, password, databaseName, backupFilePath);
            return "恢复成功";
        } catch (Exception e) {
            return "恢复失败: " + e.getMessage();
        }
    }
}
