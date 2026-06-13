package com.example.leetcode.strategy;

/**
 * 微信支付
 */
public class WechatPayment implements PaymentStrategy {

    private final String openId;

    public WechatPayment(String openId) {
        this.openId = openId;
    }

    @Override
    public void pay(int amount) {
        System.out.printf("【微信支付】用户 %s 支付了 %.2f 元%n",
                openId, amount / 100.0);
    }
}
