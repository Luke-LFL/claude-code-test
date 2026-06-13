package com.example.leetcode.strategy;

/**
 * 支付宝支付
 */
public class AlipayPayment implements PaymentStrategy {

    private final String account;

    public AlipayPayment(String account) {
        this.account = account;
    }

    @Override
    public void pay(int amount) {
        System.out.printf("【支付宝】账户 %s 支付了 %.2f 元%n",
                account, amount / 100.0);
    }
}
