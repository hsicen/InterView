package com.hsicen.interview.search.binary;

/**
 * <p>作者：Hsicen  2019/9/20 17:24
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：二分查找法
 * 时间复杂度： O(logn)    最坏O(n)  平方根
 * 空间复杂度：O(1)
 * <p>
 * 二分查找法有一定局限性：
 * 首先要求数据是能够随机访问的，其次数据要是有序的
 * 二分查找不适合太大的数据量也不适合太小的数据量
 * <p>
 * LeetCode：
 * 69：sqrt(x)
 * Input: 2.00000, 10
 * Output: 1024.00000
 * <p>
 * Input: 2.10000, 3
 * Output: 9.26100
 * <p>
 * Input: 2.00000, -2
 * Output: 0.25000
 * Explanation: 2-2 = 1/22 = 1/4 = 0.25
 * 50：Pow(x, n)
 */
public class BinarySearch {

    public static void main(String[] args) {
        int[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 34, 67, 89, 123, 567, 1234, 12345, 123456};
        System.out.println("34 is in " + search2(data, 0, data.length - 1, 34));
        System.out.println("1234 is in " + search2(data, 0, data.length - 1, 1234));
        System.out.println("88 is in " + search2(data, 0, data.length - 1, 88));
    }

    /**
     * 二分查找的非递归实现
     *
     * @param src   原数组
     * @param value 指定值
     * @return 指定值下标(没找到 - 1)
     */
    private static int search(int[] src, int value) {
        if (null == src) return -1;

        int low = 0;
        int high = src.length - 1;

        while (low <= high) {
            int mid = (low + high) / 2;

            if (value == src[mid]) {
                return mid;
            }

            if (value > src[mid]) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    /**
     * 二分查找法的递归实现
     *
     * @param src   原数组
     * @param low   起始范围
     * @param high  结束范围
     * @param value 查找值
     * @return 查找值下标
     */
    private static int search2(int[] src, int low, int high, int value) {
        if (null == src || low > high) return -1;

        int mid = (low + high) / 2;
        if (value == src[mid]) return mid;

        if (value > src[mid]) {
            return search2(src, mid + 1, high, value);
        } else {
            return search2(src, low, mid - 1, value);
        }
    }
}
