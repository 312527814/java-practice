package com.my.file;

import java.io.*;

public class FileTest {
    public static void main(String[] args) {
        File file = new File("data.txt");
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                Thread.sleep(1000*5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
