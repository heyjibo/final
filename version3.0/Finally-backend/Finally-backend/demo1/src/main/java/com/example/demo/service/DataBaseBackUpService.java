package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class DataBaseBackUpService {


    private final String backupCommandTemplate = "\"C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump\" -u %s -p%s %s > %s";
    private final String backupDir = "D:/Study/BiShe/backup/";

    public void backupDatabase(String username, String password, String databaseName, String backupFileName) throws IOException, InterruptedException {
        // 构造备份命令
        String backupCommand = String.format(backupCommandTemplate, username, password, databaseName, backupDir + backupFileName);

        // 执行备份命令
        Process process = Runtime.getRuntime().exec(backupCommand);
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("备份成功，备份文件: " + backupDir + backupFileName);
        } else {
            System.err.println("备份失败，错误码: " + exitCode);
            throw new RuntimeException("备份失败，错误码: " + exitCode);
        }
    }

    public void deleteOldBackups(int days) throws IOException {
        long currentTimeMillis = System.currentTimeMillis();
        long timeThreshold = currentTimeMillis - (days * 24 * 60 * 60 * 1000);

        File backupDirFile = new File(backupDir);
        File[] backupFiles = backupDirFile.listFiles();

        if (backupFiles != null) {
            for (File file : backupFiles) {
                if (file.lastModified() < timeThreshold) {
                    Files.delete(Paths.get(file.getAbsolutePath()));
                    System.out.println("删除旧备份文件: " + file.getAbsolutePath());
                }
            }
        }
    }
}
