package com.hsicen.interview.linklist;

import java.util.Stack;

/**
 * <p>作者：Hsicen  2019/8/27 8:40
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：回文链表
 */
public class List234 {

    /**
     * 使用快慢指针法判断单链表是否为回文链表
     *
     * @param head 表头
     * @return 是否为回文链表
     */
    public boolean isPalindrome(ListNode head) {
        //边界条件判定
        if (head == null || head.next == null) return true;

        ListNode fast = head;
        ListNode slow = fast;
        ListNode pre = null;

        while (fast != null && fast.next != null) {
            fast = fast.next.next;

            //反转慢链表
            ListNode next = slow.next;
            slow.next = pre;
            pre = slow;
            slow = next;
        }

        //链表长度奇偶性判断
        if (fast != null) slow = slow.next;

        //前后对比
        while (pre != null && slow != null) {
            if (pre.data != slow.data) return false;

            pre = pre.next;
            slow = slow.next;
        }

        return true;
    }

    /**
     * 利用栈实现回文链表的判断
     *
     * @param head 表头
     * @return 是否为回文链表
     */
    public boolean isPalindrome1(ListNode head) {
        if (head == null || head.next == null) return true;

        ListNode fast = head;
        ListNode slow = head;

        Stack<ListNode> halfNode = new Stack<>();

        //找到中间结点，前半部分入栈
        while (fast != null && fast.next != null) {
            halfNode.push(slow);
            fast = fast.next.next;
            slow = slow.next;
        }

        //后半部分与栈中元素进行比较
        while (!halfNode.isEmpty() && slow.next != null) {
            if (slow.next.data != halfNode.pop().data) return false;
            slow = slow.next;
        }

        return true;
    }
}
