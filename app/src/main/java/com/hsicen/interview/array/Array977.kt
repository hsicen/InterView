package com.hsicen.interview.array

import kotlin.math.abs

/**
 * <p>作者：Hsicen  2019/8/23 9:07
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：InterView
 *  Given an array of integers A sorted in non-decreasing order,
 *  return an array of the squares of each number, also in sorted non-decreasing order.
 *
 *  思路：由于数组是有序数组，故数组平方的最大值一定在数组的两端产生，
 *                则可以从数组的两端往中间比较，得出最大的值由新数组的末尾往头开始添加
 */

fun main() {

    var samp1 = intArrayOf(-9, -5, -4, 0, 1, 4, 5, 8)
    samp1 = sortedSquares(samp1)
    samp1.forEach { print("$it  ") }
}


fun sortedSquares(A: IntArray): IntArray {
    val size = A.size
    val result = IntArray(size)
    var start = 0
    var end = size - 1

    for (p in size - 1 downTo 0) {
        if (abs(A[start]) > abs(A[end])) {
            result[p] = A[start] * A[start]
            start++
        } else {
            result[p] = A[end] * A[end]
            end--
        }
    }

    return result
}