package com.luch.gmall.service;

import com.luch.gmall.bean.OmsOrder; /**
 * @author luch
 * @date 2019/8/28-20:37
 */
public interface OrderService {
    String generateTradeCode(String memberId);

    String checkTradeCode(String memberId, String tradeCode);

    void saveOrder(OmsOrder omsOrder);

    OmsOrder getOrderByOutTradeNo(String outTradeNo);

    void updateOrder(OmsOrder omsOrder);
}
