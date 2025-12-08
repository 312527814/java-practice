package com.my.controller;

import com.alibaba.excel.EasyExcel;
import com.my.excel.CarsMappingData;
import com.my.excel.IndexOrNameDataListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/file")
public class FileController {

    static {
        System.out.println("excel_Initializing");
    }
    @PostMapping("upload")
    public String excel(@RequestParam("file") MultipartFile file,@RequestParam("path") String path) throws IOException {
        String s = uploadFile(path, file.getInputStream());

        System.out.println("上传文件完成："+s);
        return "ddd";
    }


    public String uploadFile(String path,InputStream inputStream) {
        String resultPathStr = "";

        resultPathStr = upload(path,inputStream);

        return resultPathStr;
    }
    public String upload(String path, InputStream in) {
        try {
            String fileName = UUID.randomUUID().toString()+".pdf";
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            path = path.substring(0, path.lastIndexOf("/") + 1);
            saveFile(in, path, fileName);
            return path + fileName;
        } catch (Exception var4) {
            throw new RuntimeException("DiskStorageServiceImpl.upload.error", var4);
        }
    }
    public static int BUFFER_SIZE = 10240;
    public static synchronized void saveFile(InputStream in, String savePath, String fileName) throws IOException {
        OutputStream out = null;
        makeDirs(savePath);
        File file = new File(savePath + File.separator + fileName);
        if (file.exists()) {
            out = new BufferedOutputStream(new FileOutputStream(file, true), BUFFER_SIZE);
        } else {
            out = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
        }
        BufferedInputStream in2 = new BufferedInputStream(in, BUFFER_SIZE);
        int len = 0;
        byte[] buffer = new byte[BUFFER_SIZE];

        while((len = in2.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        in2.close();
        out.close();
    }

    public static boolean makeDirs(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            File folder = new File(filePath);
            return folder.exists() && folder.isDirectory() ? true : folder.mkdirs();
        } else {
            return false;
        }
    }
}
