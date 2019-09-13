package com.hsicen.interview.sort.compare;

import java.util.Arrays;

/**
 * <p>作者：Hsicen  2019/9/2 15:32
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：插入排序算法分析
 * 基于比较的排序算法
 * 将数组分成两个部分，有序部分和无序部分，
 * 每次取出无序数组的第一个元素，然后从后往前和有序数据进行比较找到新数据的插入位置。
 * 初始有序部分可以为数组的第一个元素
 * 总结：每次取出无序数组的一个元素，从后往前遍历有序数组寻找插入位置
 * <p>
 * 时间复杂度：最好O(n)   最坏O(n^2)  平均O(n^2)
 * 空间复杂度：O(1) 原地排序
 * 比较交换次数：逆序度
 * 稳定性：稳定排序算法
 * <p>
 * 相比于冒泡排序，插入排序的赋值操作更少
 */
public class InsertSort {

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

    private static void sort(int[] data, int size) {
        if (1 == size) return;

        int i;  //无序数组开始下标
        int j; //有序数组最后一个元素下标

        //遍历无序数组
        for (i = 1; i < size; i++) {
            int value = data[i];
            j = i - 1;

            //遍历有序数组(从后往前)，寻找插入位置
            for (; j >= 0; j--) {
                if (data[j] > value) {
                    data[j + 1] = data[j];  //移动老数据
                } else break;
            }

            data[j + 1] = value; //插入新数据
        }
    }
}
