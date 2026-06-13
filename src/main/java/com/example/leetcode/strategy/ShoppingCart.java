package com.example.leetcode.strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车——策略模式的 Context 角色
 *
 * 持有对支付策略的引用，将计算与支付行为委托给策略对象。
 */
public class ShoppingCart {

    private final List<Integer> items = new ArrayList<>();

    /** 当前选中的支付策略 */
    private PaymentStrategy paymentStrategy;

    public void addItem(int priceInFen) {
        items.add(priceInFen);
    }

    /** 运行时切换支付策略 */
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    /** 结算：计算总价并调用策略支付 */
    public void checkout() {
        int total = items.stream().mapToInt(Integer::intValue).sum();
        System.out.println("购物车总计: " + total / 100.0 + " 元");
        paymentStrategy.pay(total);
    }
}
