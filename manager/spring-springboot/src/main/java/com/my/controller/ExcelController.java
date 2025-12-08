package com.my.controller;

import com.alibaba.excel.EasyExcel;
import com.my.excel.CarsMappingData;
import com.my.excel.IndexOrNameData;
import com.my.excel.IndexOrNameDataListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@RestController
@RequestMapping("/excel")
public class ExcelController {

    static {
        System.out.println("excel_Initializing");
    }
    @GetMapping("upload")
    public String excel(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        List<CarsMappingData> objects = EasyExcel.read(inputStream, CarsMappingData.class, new IndexOrNameDataListener()).doReadAllSync();
        //EasyExcel.read(inputStream, IndexOrNameData.class, new IndexOrNameDataListener()).sheet().doRead();
        return "ddd";
    }
}
