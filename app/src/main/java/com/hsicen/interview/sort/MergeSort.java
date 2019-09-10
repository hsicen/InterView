package com.hsicen.interview.sort;

import java.util.Arrays;

/**
 * <p>作者：Hsicen  2019/9/9 20:51
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：归并排序
 */
public class MergeSort {

    public static void main(String[] args) {
        int[] data = {1, 3, 5, 2, 4, 6};
        System.out.println("data = " + Arrays.toString(data));
        merge(data, 0, 5);
        System.out.println("sort = " + Arrays.toString(data));

        int[] data1 = {9, 1, 3, 5, 2, 4, 6, 3};
        System.out.println("data1 = " + Arrays.toString(data1));
        merge(data1, 0, 7);
        System.out.println("sort = " + Arrays.toString(data1));
    }

    /**
     * 归并数组
     *
     * @param src   数组
     * @param start 起始下标
     * @param end   结束下标
     */
    private static void merge(int[] src, int start, int end) {
        if (start >= end) return;

        //递归分组
        int middle = start + (end - start) / 2;
        merge(src, start, middle);
        merge(src, middle + 1, end);

        //合并分组
        sort(src, start, middle, end);
    }

    /**
     * 合并数组
     *
     * @param dest   原数组
     * @param start  其实下标
     * @param middle 中间下标
     * @param end    结束下标
     */
    private static void sort(int[] dest, int start, int middle, int end) {
        int i = start;
        int j = middle + 1;
        int k = 0;
        int[] tmp = new int[end - start + 1];

        while (i <= middle && j <= end) {
            if (dest[i] <= dest[j]) {
                tmp[k++] = dest[i++];
            } else {
                tmp[k++] = dest[j++];
            }
        }

        int destStart = i;
        int destEnd = middle;

        if (j <= end) {
            destStart = j;
            destEnd = end;
        }

        while (destStart <= destEnd) {
            tmp[k++] = dest[destStart++];
        }

        for (i = 0; i <= end - start; i++) {
            dest[start + i] = tmp[i];
        }
    }

    /**
     * 利用哨兵合并两个数组
     *
     * @param dest   原数组
     * @param start  开始下标
     * @param middle 中间下标
     * @param end    结束下标
     */
    private static void sortBySentry(int[] dest, int start, int middle, int end) {


    }
}
