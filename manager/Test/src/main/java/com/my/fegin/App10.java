package com.my.fegin;

import java.io.*;
import java.util.Random;

public class App10 {

    public static void main(String[] args) {
        // 输入输出文件路径
        String inputFilePath = "E:\\googel\\downloads\\nginx.log7";
        String outputFilePath = "E:\\googel\\downloads\\output5.log";

        // 要过滤的 URL 路径
        String targetUrl = "/api/v2/monitor-data-service/state/monitorTopic";

        // 随机数生成器
        Random random = new Random();

        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 判断是否包含目标 URL
                if (line.contains(targetUrl)) {
                    // 90% 概率删除
                    if (random.nextDouble() < 0.98) {
                        continue; // 删除这一行
                    }
                }
                // 保留的行写入输出文件
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("处理完成，结果已写入 " + outputFilePath);
    }
}