package com.example.leetcode.strategy;

/**
 * 信用卡支付
 */
public class CreditCardPayment implements PaymentStrategy {

    private final String cardNumber;
    private final String holderName;

    public CreditCardPayment(String cardNumber, String holderName) {
        this.cardNumber = cardNumber;
        this.holderName = holderName;
    }

    @Override
    public void pay(int amount) {
        System.out.printf("【信用卡】%s 使用卡号 %s 支付了 %.2f 元%n",
                holderName, maskCard(), amount / 100.0);
    }

    private String maskCard() {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
