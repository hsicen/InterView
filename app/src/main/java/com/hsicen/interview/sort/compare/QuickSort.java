package com.hsicen.interview.sort.compare;

import java.util.Arrays;

/**
 * <p>作者：Hsicen  2019/9/10 17:39
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：快速排序
 * 分治思想
 * <p>
 * 排序思想：
 * 找到一个轴点，将数组分成两部分，大于轴点的在左边，小于轴点的在右边
 * 然后将轴点两边的数据再进行递归分区，重复上述步骤，直到分组的数据只剩一个
 * <p>
 * 时间复杂度：最好O(nlogn)   最坏O(n^2)  平均O(nlogn)
 * 空间复杂度：O(1) 原地排序
 * 稳定性：不稳定排序算法
 */
public class QuickSort {

    public static void main(String[] args) {
        int[] data = {1, 3, 5, 2, 4, 6};
        System.out.println("data = " + Arrays.toString(data));
        sort(data, 0, 5);
        System.out.println("sort = " + Arrays.toString(data));

        int[] data1 = {9, 1, 3, 5, 2, 4, 6, 3};
        System.out.println("data1 = " + Arrays.toString(data1));
        sort(data1, 0, 7);
        System.out.println("sort = " + Arrays.toString(data1));
    }

    /**
     * 递归分组
     *
     * @param src   原数组
     * @param start 开始下标
     * @param end   结束下标
     */
    private static void sort(int[] src, int start, int end) {
        if (start >= end) return;

        int pivot = partition(src, start, end);
        sort(src, start, pivot - 1);
        sort(src, pivot + 1, end);
    }

    /**
     * 数组分区操作
     * 从给定的src数组中选取一个轴点，将大于轴点的值放在左边，小于轴点的值放在右边
     *
     * @param src   原数组
     * @param start 起始下标
     * @param end   结束下标
     * @return 分区轴点下标
     */
    private static int partition(int[] src, int start, int end) {
        int pivot = src[end];
        int i = start;

        for (int j = start; j < end; j++) {
            if (src[j] < pivot) {
                if (i == j) {
                    ++i;
                } else {
                    int tmp = src[i];
                    src[i++] = src[j];
                    src[j] = tmp;
                }
            }
        }

        src[end] = src[i];
        src[i] = pivot;

        return i;
    }
}
