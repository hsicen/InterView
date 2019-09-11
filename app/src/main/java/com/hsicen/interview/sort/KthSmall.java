package com.hsicen.interview.sort;

import java.util.Arrays;
import java.util.Random;

/**
 * <p>作者：Hsicen  2019/9/11 9:15
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：寻找第K大的元素
 */
public class KthSmall {

    public static void main(String[] args) {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int size = i * 200 + 1;
            int[] tmpData = new int[size];

            for (int i1 = 0; i1 < size; i1++) {
                tmpData[i1] = random.nextInt(200);
            }

            System.out.println("data = " + Arrays.toString(tmpData));
            System.out.println("第" + (i + 1) + "大：" + findKthSmall(tmpData, i + 1));
            System.out.println("----------------------------------------------");
        }
    }

    /***
     * 寻找第K大的元素
     * @param src 原数组
     * @param k 第K大
     * @return 第K大元素
     */
    private static int findKthSmall(int[] src, int k) {
        if (src == null || src.length < k) return -1;

        //分区操作
        int pivot = partition(src, 0, src.length - 1);
        while (k != pivot + 1) {
            if (k > pivot + 1) {
                pivot = partition(src, pivot + 1, src.length - 1);
            } else {
                pivot = partition(src, 0, pivot - 1);
            }
        }

        return src[pivot];
    }


    /***
     * 数组分区操作
     * @param src 原数组
     * @param start 开始分区下标
     * @param end 结束分区下标
     * @return 分区轴点下标
     */
    private static int partition(int[] src, int start, int end) {
        int pivot = src[end];
        int i = start;

        for (int j = start; j < end; j++) {
            if (src[j] >= pivot) {  //必须为 >=,否则会出现死循环
                int tmp = src[i];
                src[i++] = src[j];
                src[j] = tmp;
            }
        }

        if (end == i) return i;
        src[end] = src[i];
        src[i] = pivot;

        return i;
    }

    private static int partition2(int[] arr, int p, int r) {
        int pivot = arr[r];

        int i = p;
        for (int j = p; j < r; j++) {
            // 这里要是 <= ，不然会出现死循环，比如查找数组 [1,1,2] 的第二小的元素
            if (arr[j] >= pivot) {
                swap(arr, i, j);
                i++;
            }
        }

        swap(arr, i, r);

        return i;
    }

    private static void swap(int[] arr, int i, int j) {
        if (i == j) {
            return;
        }

        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

}
