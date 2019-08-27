package com.hsicen.interview.linklist

/**
 * <p>作者：Hsicen  2019/8/26 22:39
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：单链表定义
 */
class SNode<T>(var data: T, var next: SNode<T>? = null) {

    /**
     * 删除指定值的结点
     */
    fun delete(data: T) {


    }

    /**
     * 头插法
     */
    fun insert(data: T) {

    }

    /**
     * 判断当前链表是否为空
     */
    fun isEmpty(): Boolean = false

    /**
     *  是否包含指定值
     */
    fun isContain(data: T): Boolean = false

}