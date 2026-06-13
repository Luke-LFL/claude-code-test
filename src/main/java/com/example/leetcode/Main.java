package com.example.leetcode;

import com.example.leetcode.strategy.*;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        demoQuickSort();
        System.out.println("\n========== 策略模式示例 ==========\n");
        demoStrategyPattern();
    }

    // ==================== 快速排序 ====================

    private static void demoQuickSort() {
        int[] arr = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("排序前: " + Arrays.toString(arr));
        quickSort(arr, 0, arr.length - 1);
        System.out.println("排序后: " + Arrays.toString(arr));
    }

    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                int tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
            }
        }
        int tmp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = tmp;
        return i + 1;
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






   

    

