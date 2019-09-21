package com.hsicen.interview.string

/**
 * <p>作者：Hsicen  2019/8/26 21:54
 * <p>邮箱：codinghuang@163.com
 * <p>作用：链表
 * <p>描述：给定一个字符串，验证它是否是回文串，只考虑字母和数字字符，可以忽略字母的大小写。
 *                       说明：本题中，我们将空字符串定义为有效的回文串。
 */

fun main() {
    println("${isPalindrome2("A man, a plan, a canal: Panama")}")
}

/**
 * 使用两个指针 从两端向中间进行比较
 */
fun isPalindrome(s: String): Boolean {
    var start = 0
    var end = s.length - 1

    while (start < end) {
        while (start < end && !Character.isLetterOrDigit(s[start])) start++
        while (start < end && !Character.isLetterOrDigit(s[end])) end--

        if (s[start].toLowerCase() != s[end].toLowerCase()) return false
        start++
        end--
    }

    return true
}


/**
 * 使用指针实现
 */
fun isPalindrome2(s: String): Boolean {
    val result = s.filter {
        it.isLetterOrDigit()
    }.toLowerCase()

    println("result:$result")

    var start = 0
    var end = result.length - 1

    while (start < end) {
        if (result[start] != result[end]) return false
        start++
        end--
    }

    return true
}

