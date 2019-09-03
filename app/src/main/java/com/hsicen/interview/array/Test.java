package com.hsicen.interview.array;

import java.util.Arrays;

/**
 * <p>作者：Hsicen  2019/8/25 22:42
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：InterView
 */
public class Test {

    public static void main(String[] args) {
        String str1 = "123";
        String str2 = "123";

        System.out.println("1==   " + str1 == str2);
        System.out.println("equals   " + str1.equals(str2));

        int[] data = {1, 2, 3, 4, 5, 6, 7};

        System.out.println("main: " + Arrays.hashCode(data));
        equalArray(data);
        System.out.println("main: " + Arrays.hashCode(data));
    }

    private static void equalArray(int[] src) {
        System.out.println("method: " + Arrays.hashCode(src));
        src = new int[10];
        System.out.println("method: " + Arrays.hashCode(src));
    }
}
