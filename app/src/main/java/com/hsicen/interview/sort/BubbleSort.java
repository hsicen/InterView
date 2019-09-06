package com.hsicen.interview.sort;

import java.util.Arrays;

/**
 * <p>作者：Hsicen  2019/9/2 14:45
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：冒泡排序分析
 * 基于比较的排序算法
 * 每次从头开始比较相邻的两个元素，然后交换位置，每一次循环比较n次，会有一个数到达最终位置
 * 进行n次循环后，排序结束
 * 提前退出：当进行一次比较过后没有发生交换则可以提前退出
 * <p>
 * 时间复杂度(最好，最坏，平均)：最好O(n)    最坏O(n^2)    平均O(n^2)
 * 空间复杂度(原地排序)：O(1)
 * 稳定性：稳定        某组数据根据某一key进行排序，具有相同key值的元素在排序前后的相对位置不会发生变化
 * 利用稳定性可以简化多属性排序
 * 有序度：相邻两个数前面的值小于等于后面的值
 * 逆序度(交换次数)：和有序度计算相反
 * 满有序度：n*(n-1)/2 = 有序度+逆序度
 */
public class BubbleSort {

    public static void main(String[] args) {

        int[] data = {4, 5, 6, 3, 2, 1};
        System.out.println("data = " + Arrays.toString(data));
        System.out.println("sort() = " + Arrays.toString(sort(data, 6)));

        int[] data2 = {6, 8, 4, 5, 6, 3, 2, 1};
        System.out.println("data2 = " + Arrays.toString(data2));
        System.out.println("sort() = " + Arrays.toString(sort(data2, 8)));

        int[] data3 = {1, 2, 3, 4, 5, 6, 7, 8};
        System.out.println("data3 = " + Arrays.toString(data3));
        System.out.println("sort() = " + Arrays.toString(sort(data3, 8)));
    }

    /**
     * 冒泡排序的简单实现
     *
     * @param data 待排序树组
     * @param size 数组大小
     * @return 排好序的数组
     */
    private static int[] sort(int[] data, int size) {
        if (1 == size) return data;

        //用于标记一次循环位置是否改变
        boolean isChange;
        int temp;

        for (int i = 0; i < size; i++) {
            isChange = false;
            for (int j = 0; j < size - i - 1; j++) {
                if (data[j] > data[j + 1]) {
                    temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;

                    isChange = true;
                }
            }

            if (!isChange) break;
        }

        return data;
    }

}
