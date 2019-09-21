package com.hsicen.interview.linklist

/**
 * <p>作者：Hsicen  2019/8/27 8:13
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：请判断一个链表是否为回文链表
 *
 * 输入: 1->2                         输出: false
 * 输入: 1->2->2->1           输出: true
 */

/**
 * 利用半栈法实现
 * 利用快慢指针找到中间结点，同时翻转慢指针所遍历的前半部分
 * 然后重置快指针指向表头   慢指针不动，顺序比较快慢指针所指向的值
 */
fun isPalindrome1(head: ListNode?): Boolean {
    if (head?.next == null) return true

    var fast: ListNode? = head
    var slow: ListNode? = head
    var pre: ListNode? = null

    while (fast?.next != null) {
        fast = fast.next?.next

        //反转
        val next = slow?.next
        slow?.next = pre
        pre = slow
        slow = next
    }

    if (fast != null) slow = slow?.next

    while (pre != null && slow != null) {
        if (pre.data != slow.data) return false

        pre = pre.next
        slow = slow.next
    }

    return true
}

fun main() {

}