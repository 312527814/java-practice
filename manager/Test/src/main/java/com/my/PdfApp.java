package com.my;

import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;

public class PdfApp {
    public static void main( String[] args )
    {
        for (int i = 1; i <6 ; i++) {
            PdfDocument document = new PdfDocument();
            String name="八上北师大版期末测试卷"+i;
            document.loadFromFile("F:\\shijuan\\"+name+".pdf");
            document.saveToFile("F:\\shijuan\\"+name+".docx", FileFormat.DOCX);
            document.close();
            System.out.println( "Hello World!" );
        }

    }
}
