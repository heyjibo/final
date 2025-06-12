package com.example.demo.entity;

import com.example.demo.service.DataBaseBackUpService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class BackUpTimely {

    private final DataBaseBackUpService backupService;

    public BackUpTimely(DataBaseBackUpService backupService) {
        this.backupService = backupService;
    }

    @Scheduled(cron = "0 20 2 * * *") // 每天凌晨2点执行
    public void performBackup() {
        String username = "root";
        String password = "123465";
        String databaseName = "dp";
        String backupFileName = "backup_" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".sql";

        try {
            backupService.backupDatabase(username, password, databaseName, backupFileName);
            backupService.deleteOldBackups(7);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}