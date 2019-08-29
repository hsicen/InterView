package com.hsicen.interview.linklist;

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
            if (pre.val != slow.val) return false;

            pre = pre.next;
            slow = slow.next;
        }

        return true;
    }


    /**
     * 单链表数据结构定义
     */
    class ListNode {
        int val;
        ListNode next;

        ListNode(int data) {
            this.val = data;
        }
    }
}
