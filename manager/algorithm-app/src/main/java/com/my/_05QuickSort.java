package com.my;

/**
 * @program:
 * @description:快速排序 左边排好序+右边排好序+merge让整体有序
 * <p>
 * 1 将数组进行partition,选择最后一个元素作为基准，将小于等于基准元素值的数据都方再左边（大于基准值的数据自然就再右边）。
 * 2 每次partition都可以确定一个元素再数组中的位置
 * 3 将确定位置的数的左边和右边分别做partition，最后确定所有元素的位置
 */
public class _05QuickSort {
    public static void main(String[] args) {
        int[] arr = new int[]{2, 3, 1, 5, 7, 2, 4};
        process(arr, 0, arr.length - 1,"asc");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + ",");
        }
        System.out.println("\r\n..............................");
    }

    private static void process(int[] arr, int l, int r,String sort) {
        if (l >= r) {
            return;
        }
        int m ;
        if (sort == "asc") {
            m=ascPartition(arr, l, r);
        } else {
            m=desPartition(arr, l, r);
        }
        process(arr, l, m - 1,sort);
        process(arr, m + 1, r,sort);
    }


    /**
     * 最后一个元素作为基准值，遍历数组，遇到<=基准值的数据，交换index和小于区前一个元素位置，小于区向前扩。直到遍历完倒数第二个元素结束。
     * 交换数组最后一个元素和小于区前一个元素，返回小于区前一个元素的索引
     * @param arr
     * @param l
     * @param r
     * @return
     */
    private static int ascPartition(int[] arr, int l, int r) {
        int pivot = arr[r]; // 选择最后一个元素作为基准
        int less = l - 1; // 较大元素的索引
        int index=l;
        while (index < r) {
            if (arr[index] <= pivot) {
                less++;
                swap(arr, less, index);

            }
            index++;
        }
        swap(arr, less + 1, r); // 将基准元素放到其最终位置
        return less + 1;
    }

    /**
     *
     * @param arr
     * @param l
     * @param r
     * @return
     */
    private static int desPartition(int[] arr, int l, int r) {
        int pivot = arr[r]; // 选择最后一个元素作为基准
        int more = l - 1; // 较大元素的索引
        int index=l;
        while (index < r) {
            if (arr[index] >= pivot) {
                more++;
                swap(arr, more, index);
            }
            index++;
        }
        swap(arr, more + 1, r); // 将基准元素放到其最终位置
        return more + 1;
    }

    private static void swap(int[] arr, int lp, int rp) {
        int tem = arr[lp];
        arr[lp] = arr[rp];
        arr[rp] = tem;
    }

}
