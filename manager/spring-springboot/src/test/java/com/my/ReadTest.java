package com.my;

import com.alibaba.excel.EasyExcel;
import com.my.excel.IndexOrNameData;
import com.my.excel.IndexOrNameDataListener;
import org.junit.Test;

/**
 * 描 述: <br/>
 * 作 者: liuliang14<br/>
 * 创 建:2022年05月27⽇<br/>
 * 版 本:v1.0.0<br> *
 * 历 史: (版本) 作者 时间 注释 <br/>
 */
public class ReadTest {
    static {
        System.out.println("ReadTest2..........");
    }
    @Test
    public void indexOrNameRead() {
        String fileName ="/Users/ll/Desktop/test.xlsx";
        // 这里默认读取第一个sheet
        EasyExcel.read(fileName, IndexOrNameData.class, new IndexOrNameDataListener()).sheet().doRead();
    }
}
