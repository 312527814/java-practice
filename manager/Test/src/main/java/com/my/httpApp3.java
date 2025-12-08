package com.my;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class httpApp3 {

    private static final String BOUNDARY = "boundary";
    private static final String LINE_FEED = "\r\n";

    public static void main(String[] args) throws Exception {
        String requestURL = "http://117.66.237.176:9832/iot-service/videoAnalysis/receiveHaiKangLiGangVideo";
        String jsonPart = buildJsonPart();
        String imagePath = "E:\\googel\\downloads\\bw-svc-upms-v202504172206.zip"; // 替换为你的图片路径

        URL url = new URL(requestURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        try (
                OutputStream outputStream = httpConn.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true)
        ) {
            // 添加 JSON 部分
            addFormField(writer, "RecognizeResult", jsonPart);

            // 添加图片部分
            addFilePart(writer, outputStream, "capturePic", imagePath);

            // 结束边界
            writer.append("--").append(BOUNDARY).append("--").append(LINE_FEED);
            writer.flush();
        }

        // 获取响应
        int responseCode = httpConn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            System.out.println("Server Response: " + response.toString());
        }
    }

    private static String buildJsonPart() {
        return "{\n" +
                "  \"ipAddress\": \"192.168.8.64\",\n" +
                "  \"ipv6Address\": \"\",\n" +
                "  \"portNo\": 8000,\n" +
                "  \"protocol\": \"HTTP\",\n" +
                "  \"macAddress\": \"dc:07:f8:1a:4e:ea\",\n" +
                "  \"channelID\": 1,\n" +
                "  \"dateTime\": \"2025-06-27T17:34:05+08:00\",\n" +
                "  \"deviceID\": \"FR7090686\",\n" +
                "  \"activePostCount\": 1,\n" +
                "  \"eventState\": \"active\",\n" +
                "  \"eventType\": \"backDuty\",\n" +
                "  \"eventDescription\": \"Fire Control Recognize Result\",\n" +
                "  \"BackDuty\": {\n" +
                "    \"contentID\": \"image\",\n" +
                "    \"capturePicture\": {\n" +
                "      \"resourcesContentType\": \"binary\",\n" +
                "      \"resourcesContent\": \"image\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    private static void addFormField(PrintWriter writer, String name, String value) {
        writer.append("--").append(BOUNDARY).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED);
        writer.append("Content-Type: application/json; charset=UTF-8").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    private static void addFilePart(PrintWriter writer, OutputStream output, String fieldName, String filePath) throws IOException {
        String fileName = new File(filePath).getName();

        writer.append("--").append(BOUNDARY).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName)
                .append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED);
        writer.append("Content-Type: image/jpeg").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
        }

        writer.append(LINE_FEED);
        writer.flush();
    }
}