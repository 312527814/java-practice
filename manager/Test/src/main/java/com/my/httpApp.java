package com.my;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class httpApp {
    public static void main(String[] args) {
        try {
//            URL url = new URL("http://localhost:80/videoAnalysis/receiveHaiKangLiGangVideo");
            URL url = new URL("http://117.66.237.176:9832/iot-service/videoAnalysis/receiveHaiKangLiGangVideo");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(1);
            connection.setReadTimeout(1);
            // 设置请求方法
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // 启用输出流
            connection.setDoOutput(true);

            // 请求体
            String jsonInputString = "{\"key\": \"value\"}";

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 获取响应
            int status = connection.getResponseCode();
            System.out.println("Status code: " + status);

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response: " + response.toString());
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
