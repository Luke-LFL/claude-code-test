package com.example.leetcode.strategy;

/**
 * 策略接口——所有支付方式的公共接口
 */
public interface PaymentStrategy {

    /**
     * 支付
     * @param amount 支付金额（分）
     */
    void pay(int amount);
}
