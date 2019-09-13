package com.hsicen.interview.sort.compare;

import java.util.Arrays;

/**
 * <p>作者：Hsicen  2019/9/3 10:12
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：选择排序算法分析
 * 基于比较的排序算法
 * <p>
 * 将数组分成两个部分，有序部分和无序部分，
 * 每次遍历无序数组，获得最小/大的元素和无序数组的第一个元素交换位置
 * 初始化时，整个数组都是无序数组
 * 总结：从前往后遍历无序数组找到最值元素，依次插入有序数组
 * <p>
 * 时间复杂度：最好O(n^2)   最坏O(n^2)  平均O(n^2)
 * 空间复杂度：O(1) 原地排序
 * 比较交换次数：逆序度
 * 稳定性：不稳定排序算法
 */
public class SelectSort {

    public static void main(String[] args) {
        int[] data = {1, 3, 5, 2, 4, 6};
        System.out.println("data = " + Arrays.toString(data));
        sort(data, 6);
        System.out.println("sort = " + Arrays.toString(data));

        int[] data1 = {9, 1, 3, 5, 2, 4, 6, 3};
        System.out.println("data1 = " + Arrays.toString(data1));
        sort(data1, 8);
        System.out.println("sort = " + Arrays.toString(data1));
    }

    private static void sort(int[] src, int size) {
        if (1 == size) return;

        //最小元素下标
        int minIndex;
        int temp;

        //先遍历无序数组，找到最小的元素
        for (int i = 0; i < size; i++) {
            minIndex = i; //记录每次开始比较的元素
            for (int j = i + 1; j < size; j++) {
                //遍历无序数组，找到最小元素
                if (src[minIndex] > src[j]) minIndex = j;
            }

            //检查最小元素位置是否发生变化
            if (minIndex != i) {
                temp = src[minIndex];
                src[minIndex] = src[i];
                src[i] = temp;
            }
        }
    }
}
