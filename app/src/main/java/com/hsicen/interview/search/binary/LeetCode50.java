package com.hsicen.interview.search.binary;

/**
 * <p>作者：Hsicen  2019/9/27 16:27
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：求一个数的幂
 * https://leetcode.com/problems/powx-n/
 * <p>
 * Implement pow(x, n), which calculates x raised to the power n (xn).
 * <p>
 * Input: 2.00000, 10
 * Output: 1024.00000
 * <p>
 * Input: 2.10000, 3
 * Output: 9.26100
 * <p>
 * Input: 2.00000, -2
 * Output: 0.25000
 * Explanation: 2^-2 = 1/22 = 1/4 = 0.25
 * <p>
 * Note:
 * -100.0 < x < 100.0
 * n is a 32-bit signed integer, within the range [−2^31, 2^31 − 1]
 */
public class LeetCode50 {

    public static void main(String[] args) {
        System.out.println("3 的 3次幂为： " + myPow(3, 3));
        System.out.println("2 的 10次幂为： " + myPow(2, 10));
        /*System.out.println("4 的 1次幂为： " + myPow(4, 1));
        System.out.println("100 的 32次幂为： " + myPow(100, 32));
        System.out.println("-100 的 32次幂为： " + myPow(-100, 32));
        System.out.println("100 的31次幂为： " + myPow(100, 31));*/
    }

    private static double myPow(double x, int n) {
        //边界条件判定
        if (0 == x) return 0;  //0的任何次幂都为0
        if (1 == x) return 1;  //1的任何次幂都为1
        if (0 == n) return 1;  //任何非0数的0次幂都为1

        double result = 1;

        if (n < 0) {
            for (int i = 0; i < -n; i++) {
                result = result * x;
            }

            result = 1 / result;
        } else {
            for (int i = 0; i < n; i++) {
                result = result * x;
            }
        }

        return result;
    }
}
