

import java.util.Arrays;

public class MemoryAllocater3 {
    public static void main(String[] args) {
        System.out.println("Starting memory allocation...");


        int size = 1024 * 1024 * 1024; // 1 GB

        for (int i = 0; i <4 ; i++) {
            byte[] bigArray = new byte[size];

            Arrays.fill(bigArray, (byte) 1);
            System.out.println("Allocated " + size / (1024 * 1024) + " MB of memory.");
        }



        System.out.println("Memory is allocated. Keeping it in use for 10 minutes...");

        try {

            Thread.sleep(10 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Done.");
    }
}