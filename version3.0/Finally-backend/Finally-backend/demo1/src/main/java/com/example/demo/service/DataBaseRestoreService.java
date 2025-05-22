package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class DataBaseRestoreService {

    private final String restoreCommandTemplate = "mysql -u %s -p%s %s < %s";

    public void restoreDatabase(String username, String password, String databaseName, String backupFilePath) throws IOException, InterruptedException {
        // 构造恢复命令
        String restoreCommand = String.format(restoreCommandTemplate, username, password, databaseName, backupFilePath);

        // 执行恢复命令
        Process process = Runtime.getRuntime().exec(restoreCommand);
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("恢复成功，恢复文件: " + backupFilePath);
        } else {
            System.err.println("恢复失败，错误码: " + exitCode);
            throw new RuntimeException("恢复失败，错误码: " + exitCode);
        }
    }
}