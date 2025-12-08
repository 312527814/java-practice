package com.my;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

public class httpApp2 {
    private final String boundary = "===" + System.currentTimeMillis() + "===";
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    public httpApp2(String requestURL, String charset)
            throws IOException {
        this.charset = charset;

        // 创建 URL 对象
        URL url = new URL(requestURL);
        // 打开连接
        httpConn = (HttpURLConnection) url.openConnection();
        // 设置通用属性
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // 表示有输出流
        httpConn.setDoInput(true); // 表示有输入流
        httpConn.setConnectTimeout(10000);
        httpConn.setReadTimeout(100000);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Connection", "close"); // 确保不使用 Keep-Alive
        httpConn.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        // 创建输出流
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }

    /**
     * 添加普通文本参数
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * 添加文件参数
     */
    public void addFilePart(String fieldName, String filePath) throws IOException {
        File uploadFile = new File(filePath);
        String fileName = uploadFile.getName();

        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName
                + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        Files.copy(uploadFile.toPath(), outputStream);
        outputStream.flush();

        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * 完成请求
     */
    public String finish() throws IOException {
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        int responseCode = httpConn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), charset))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    public static void main(String[] args) {
        try {
            httpApp2 multipartRequest = new httpApp2(
                    "http://117.66.237.176:9832/iot-service/videoAnalysis/receiveHaiKangLiGangVideo", "UTF-8");

            // 添加文本参数
            multipartRequest.addFormField("username", "JohnDoe");
            multipartRequest.addFormField("password", "12345");

            // 添加文件参数
            multipartRequest.addFilePart("file", "E:\\1751448614919.jpg");

            // 发送请求并获取响应
            String response = multipartRequest.finish();
            System.out.println("Server Response: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}