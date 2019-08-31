package com.luch.gmall.service;

import com.luch.gmall.bean.PaymentInfo;

import java.util.Map;

/**
 * @author luch
 * @date 2019/8/29-17:30
 */
public interface PaymentService {
    void savePaymentInfo(PaymentInfo paymentInfo);

    void updatePayment(PaymentInfo paymentInfo);

    void sendPaymentResultCheckDelayQueue(String outTradeNo, int i);

    Map<String,Object> checkAlipayPayment(String out_trade_no);
}
