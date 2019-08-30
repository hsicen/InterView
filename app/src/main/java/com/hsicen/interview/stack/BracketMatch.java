package com.hsicen.interview.stack;

import java.util.Stack;

/**
 * <p>作者：Hsicen  2019/8/30 13:57
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：括号匹配
 */
public class BracketMatch {

    public static void main(String[] args) {

        System.out.println("[{((({([{{[([[[[[[({{[()]}})]]]]]])]}}])})))}]  is  match " + bracketMatch("[{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[()]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}]"));
        System.out.println("[{((({([{{[([[[[[[({{[()]}})]]]]]])]}}])})))}]  is  match " + bracketMatch1("[{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[([{((({([{{[([[[[[[({{[()]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}])]}})]]]]]])]}}])})))}]"));
    }

    private static boolean bracketMatch(String str) {
        if (str == null) return false;
        if (0 == str.length()) return true;

        Stack<Character> bracketStack = new Stack<>();
        for (int i = 0; i < str.length(); i++) {
            char indexChar = str.charAt(i);

            if (indexChar == '(' || indexChar == '[' || indexChar == '{') {
                bracketStack.push(indexChar);
            } else {
                if (indexChar == ')' && !bracketStack.isEmpty() && bracketStack.pop() != '(') {
                    return false;
                } else if (indexChar == ']' && !bracketStack.isEmpty() && bracketStack.pop() != '[') {
                    return false;
                } else if (indexChar == '}' && !bracketStack.isEmpty() && bracketStack.pop() != '{') {
                    return false;
                }
            }
        }

        return bracketStack.isEmpty();
    }

    private static boolean bracketMatch1(String str) {
        int length = str.length();
        if (0 == length) return true;
        if (length % 2 != 0) return false;

        Stack<Character> bracketStack = new Stack<>();
        Character temp;

        for (int i = 0; i < length; i++) {
            char indexChar = str.charAt(i);

            if (indexChar == '(' || indexChar == '[' || indexChar == '{') {
                bracketStack.push(indexChar);
            } else {
                if (bracketStack.isEmpty()) return false;

                temp = bracketStack.pop();
                if (indexChar == ')') {
                    if (temp != '(') return false;
                } else if (indexChar == ']') {
                    if (temp != '[') return false;
                } else if (indexChar == '}') {
                    if (temp != '{') return false;
                }
            }
        }

        return bracketStack.isEmpty();
    }
}
