package com.hsicen.interview.sort.compare;

import java.util.Arrays;

/**
 * <p>作者：Hsicen  2019/9/9 20:51
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：归并排序
 * 分治思想：将大问题细分成小问题，小问题解决了，大问题就解决了
 * <p>
 * 排序思想：
 * 首先利用递归将每个数组对半分成两个数组O(logn),直到一个元素为一个数组
 * 然后递归合并数组，将上面递归分成的数组一层一层的合并O(n)
 * <p>
 * 时间复杂度：最好O(nlogn)   最坏O(nlogn)  平均O(nlogn)
 * 空间复杂度：O(n)
 * 稳定性：稳定排序算法
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

        //递归将每个数组对半分成两个数组O(logn),直到一个元素为一个数组
        int middle = start + (end - start) / 2;
        merge(src, start, middle);
        merge(src, middle + 1, end);

        //递归合并数组，将上面递归分成的数组一层一层的合并O(n)
        sortBySentry(src, start, middle, end);
    }

    /**
     * 合并两个有序数组
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

        //合并有序数组
        while (i <= middle && j <= end) {
            if (dest[i] <= dest[j]) {
                tmp[k++] = dest[i++];
            } else {
                tmp[k++] = dest[j++];
            }
        }

        //判断未合并完的数组
        int destStart = (j <= end) ? j : i;
        int destEnd = (j <= end) ? end : middle;

        while (destStart <= destEnd) {
            tmp[k++] = dest[destStart++];
        }

        //将排序好的数组拷贝到原数组中
        for (i = 0; i <= end - start; i++) {
            dest[start + i] = tmp[i];
        }
    }

    /**
     * 利用哨兵合并两个有序数组
     *
     * @param dest   原数组
     * @param start  开始下标
     * @param middle 中间下标
     * @param end    结束下标
     */
    private static void sortBySentry(int[] dest, int start, int middle, int end) {
        int[] leftSrc = new int[middle - start + 2];
        int[] rightSrc = new int[end - middle + 1];

        //左边数组添加哨兵
        leftSrc[middle - start + 1] = Integer.MAX_VALUE;
        if (middle - start + 1 >= 0)
            System.arraycopy(dest, start, leftSrc, 0, middle - start + 1);

        //右边数组添加哨兵
        rightSrc[end - middle] = Integer.MAX_VALUE;
        for (int i = 0; i < end - middle; i++) {
            rightSrc[i] = dest[middle + 1 + i];
        }

        //数组合并
        int leftStart = 0;
        int rightStart = 0;
        int index = start;
        while (index <= end) {
            if (leftSrc[leftStart] <= rightSrc[rightStart]) {
                dest[index++] = leftSrc[leftStart++];
            } else {
                dest[index++] = rightSrc[rightStart++];
            }
        }
    }
}
