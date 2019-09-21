package com.hsicen.interview.array

/**
 * <p>作者：Hsicen  2019/8/24 18:58
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：
 *
 *  斐波那契数，通常用 F(n) 表示，形成的序列称为斐波那契数列。
 *  该数列由 0 和 1 开始，后面的每一项数字都是前面两项数字的和。
 *
 *   F(0) = 0,   F(1) = 1
 *   F(N) = F(N - 1) + F(N - 2), 其中 N > 1.
 */

fun main() {

    println(" 2 is ${fib(2)} ")
    println(" 3 is ${fib(3)} ")
}

fun fib(N: Int): Int {

    if (0 == N || 1 == N) {
        return N
    }

    return fib(N - 1) + fib(N - 2)
}

