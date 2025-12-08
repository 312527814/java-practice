package com.my;

/**
 * @program:
 * @description:选择排序
 * 1.依次从数组或者队列的开头取出第一个数，第二个，第n个和后面的数进行比较。
 * 2.把每次比较得到的最大值或者最小值放在数组的最前面。
 * @author: liang.liu
 * @create: 2021-10-25 14:07
 */
public class _02SelectionSort {
    public static void main(String[] args) {
        int[] arr = new int[]{2, 3, 1, 5, 7, 2, 4};
//        aseSort(arr);
        desSort(arr);
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    /*
     * 升序
     * */
    public static void aseSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            //最小值在哪个下标上  i～n-1
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {//i ~ N-1 上找最小值的下标
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            swap(arr, minIndex, i);
        }
    }

    /*
     * 降序
     * */
    public static void desSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] > arr[maxIndex]) {
                    maxIndex = j;
                }
            }
            swap(arr, maxIndex, i);
        }
    }


    public static void swap(int[] arr, int index1, int index2) {
        int temp = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = temp;
    }
}
