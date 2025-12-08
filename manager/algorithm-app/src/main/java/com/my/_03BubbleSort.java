package com.my;

/**
 * @program:
 * @description:冒泡排序
 * 1 比较相邻的元素，如果第一个比第二个大，就交换他们， 把大的放到后面再和后面的其他元素比较。
 * 2 对每一对相邻元素做同样的工作，从开始第一对到结尾的最后一对。每次执行完都会产生一个最大数。
 * 3 每次比较完都会使需要比较的数少 1 ，一直进行到只剩最后一个,即 比较次数从n-1 一直到1。
 * 4 n个数需要进行的循环次数从n - 1次到1次。
 *
 *
 * 与选择排期的区别？
 *
 * 区别在于：在交换的方式上
 * 冒泡算法，每次比较如果发现较小的元素在后面，就交换两个相邻的元素。
 * 而选择排序算法的改进在于：先并不急于调换位置，先从A[m+1]开始逐个检查，看哪个数最小就记下该数所在的坐标min，等一躺扫描完毕，再把A[m]和A[min]对调，这时A[m+1]到A[n]中最小的数据就换到了最前面的位置。
 * 所以，选择排序每扫描一遍数组，只需要一次真正的交换，而冒泡可能需要很多次。比较的次数一样的。
 * @author: liang.liu
 * @create: 2021-10-25 14:07
 */
public class _03BubbleSort {
    public static void main(String[] args) {
        int[] arr = new int[]{2, 3, 1, 5, 7, 2, 4};
        aseSort(arr);
//        desSort(arr);
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    /*
     * 升序
     * */
    public static void aseSort(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (arr[j + 1] > arr[j]) {
                    swap(arr, j + 1, j);
                }

            }
        }
    }

    /*
     * 降序
     * */
    public static void desSort(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (arr[j + 1] < arr[j]) {
                    swap(arr, j + 1, j);
                }

            }
        }
    }


    public static void swap(int[] arr, int index1, int index2) {
        int temp = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = temp;
    }
}
