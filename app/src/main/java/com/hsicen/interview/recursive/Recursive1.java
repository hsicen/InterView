package com.hsicen.interview.recursive;

/**
 * <p>作者：Hsicen  2019/8/30 16:56
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：爬楼梯(n个台阶，如果一次可以跨1个台阶也可以跨2个台阶，问有多少种走法)
 * 要点：递归体
 * 递归终止条件
 */
public class Recursive1 {

    public static void main(String[] args) {

        System.out.println("3个台阶有  " + climbStairs(3) + "  种跨法");
        System.out.println("13个台阶有  " + climbStairs(13) + "  种跨法");
        System.out.println("23个台阶有  " + climbStairs(23) + "  种跨法");
        System.out.println("33个台阶有  " + climbStairs(33) + "  种跨法");
        System.out.println("43个台阶有  " + climbStairs(43) + "  种跨法");
    }

    /**
     * 爬楼梯递归实现
     *
     * @param step 楼梯数
     * @return 方法数
     */
    private static int climbStairs(int step) {
        //n个台阶，可以先跨1个台阶+(n-1)个台阶的跨法或者先跨2个台阶+(n-2)个台阶的跨法;以此类推
        //最后1个台阶有1种跨法，2个台阶有2种跨法

        if (1 == step) return 1;
        if (2 == step) return 2;

        return climbStairs(step - 1) + climbStairs(step - 2);
    }

    /**
     * 爬楼梯非递归实现
     *
     * @param step 楼梯数
     * @return 方法数
     */
    private static int climbStairs2(int step) {
        if (1 == step) return 1;
        if (2 == step) return 2;

        int allWays = 0;
        int pre = 2;
        int prePre = 1;

        for (int i = 0; i < step; i++) {
            allWays = pre + prePre;
            prePre = pre;
            pre = allWays;
        }

        return allWays;
    }
}
