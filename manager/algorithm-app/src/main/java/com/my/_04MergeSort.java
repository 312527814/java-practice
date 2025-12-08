package com.my;


/**
 * @program:
 * @description:归并排序
 * 左边排好序+右边排好序+merge让整体有序
 *
 * 1 将数组从中间分左右两部分
 * 2 左右两边分别排好序
 * 3 左右合并让总体有序
 */
public class _04MergeSort {
    public static void main(String[] args) {
        int[] arr = new int[]{2, 3, 1, 5, 7, 2, 4};
        aseSort(arr);
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + ",");
        }
        System.out.println("\r\n..............................");


        int[] arr2 = new int[]{2, 3, 1, 5, 7, 2, 4};
        descSort(arr2);

        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + ",");
        }

    }

    private static void aseSort(int[] arr) {

        process(arr, 0, arr.length - 1, "ase");
    }

    private static void descSort(int[] arr) {

        process(arr, 0, arr.length - 1, "desc");
    }

    private static void process(int[] arr, int l, int r, String sort) {
        if (r - l <= 0) {
            return;
        }
        int middle = (r + l) / 2;
//        0123456
        process(arr, l, middle, sort);
        process(arr, middle + 1, r, sort);
        if (sort == "desc") {
            desMerge(arr, l, middle, r);
        } else {
            aseMerge(arr, l, middle, r);
        }


    }

    private static void aseMerge(int[] arr, int l, int middle, int r) {
        int p1 = l;
        int p2 = middle + 1;

        int[] help = new int[r - l + 1];
        int ph = 0;
        while (p1 <= middle && p2 <= r) {
            if (arr[p1] < arr[p2]) {
                help[ph++] = arr[p1];
                p1++;
            } else {
                help[ph++] = arr[p2];
                p2++;
            }
        }
        while (p1 <= middle) {
            help[ph++] = arr[p1++];
        }
        while (p2 <= r) {
            help[ph++] = arr[p2++];
        }
        for (int i = 0; i < help.length; i++) {
            arr[l++] = help[i];
        }
    }


    private static void desMerge(int[] arr, int l, int middle, int r) {
        int p1 = l;
        int p2 = middle + 1;

        int[] help = new int[r - l + 1];
        int ph = 0;
        while (p1 <= middle && p2 <= r) {
            if (arr[p1] < arr[p2]) {
                help[ph++] = arr[p2];
                p2++;
            } else {
                help[ph++] = arr[p1];
                p1++;
            }
        }
        while (p1 <= middle) {
            help[ph++] = arr[p1++];
        }
        while (p2 <= r) {
            help[ph++] = arr[p2++];
        }
        for (int i = 0; i < help.length; i++) {
            arr[l++] = help[i];
        }
    }

}
