package com.example.leetcode;

import com.example.leetcode.strategy.*;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        demoBubbleSort();
        System.out.println("\n========== 策略模式示例 ==========\n");
        demoStrategyPattern();
    }

    // ==================== 冒泡排序 ====================

    private static void demoBubbleSort() {
        int[] arr = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("排序前: " + Arrays.toString(arr));
        bubbleSort(arr);
        System.out.println("排序后: " + Arrays.toString(arr));
    }

    public static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
    }

    // ==================== 策略模式示例 ====================

    /**
     * 演示策略模式：购物车在运行时切换不同支付方式
     */
    private static void demoStrategyPattern() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(2999);   // 商品A 29.99元
        cart.addItem(1500);   // 商品B 15.00元
        cart.addItem(8800);   // 商品C 88.00元

        // 用信用卡支付
        cart.setPaymentStrategy(new CreditCardPayment("6222021234567890", "张三"));
        cart.checkout();

        System.out.println();

        // 切换为支付宝
        cart.setPaymentStrategy(new AlipayPayment("alice@example.com"));
        cart.checkout();

        System.out.println();

        // 切换为微信支付
        cart.setPaymentStrategy(new WechatPayment("wx_openid_abc123"));
        cart.checkout();
    }

}






   

    

