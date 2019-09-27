package com.hsicen.interview.search.binary;

/**
 * <p>作者：Hsicen  2019/9/27 16:21
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：求一个数的平方(取整)
 * https://leetcode.com/problems/sqrtx/
 * <p>
 * Compute and return the square root of x, where x is guaranteed to be a non-negative integer.
 * Since the return type is an integer, the decimal digits are truncated and only the integer part of the result is returned.
 * <p>
 * Input: 4
 * Output: 2
 * <p>
 * Input: 8
 * Output: 2
 * Explanation: The square root of 8 is 2.82842..., and since
 * the decimal part is truncated, 2 is returned.
 */
public class LeetCode69 {

    public static void main(String[] args) {

        System.out.println("-9 的平方根为：" + mySqrt(-9));
        System.out.println("1 的平方根为：" + mySqrt(1));
        System.out.println("4 的平方根为：" + mySqrt(4));
        System.out.println("8 的平方根为：" + mySqrt(8));
        System.out.println("9 的平方根为：" + mySqrt(9));
        System.out.println("10 的平方根为：" + mySqrt(10));
        System.out.println("24 的平方根为：" + mySqrt(24));
        System.out.println("2147395598 的平方根为：" + mySqrt(2147395598));
    }

    /**
     * 求一个数的平方根取整数
     *
     * @param x 值
     * @return 平方根
     */
    private static int mySqrt(int x) {
        //边界条件判定
        if (0 >= x) return 0;
        if (1 == x) return 1;

        int low = 0;
        int high = x / 2; //减少循环次数

        while (low + 1 < high) {
            int mid = low + (high - low) / 2;
            if (mid == x / mid) return mid;   //mid * mid 会超过int最大值

            if (mid < x / mid) {
                low = mid;
            } else {
                high = mid;
            }
        }

        if (high <= x / high) return high;
        return low;
    }
}
