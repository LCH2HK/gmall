package com.luch.gmall.order.mq;

import com.luch.gmall.bean.OmsOrder;
import com.luch.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * @author luch
 * @date 2019/8/30-21:04
 */
@Component
public class OrderServiceMqListener {

    @Autowired
    OrderService orderService;

    @JmsListener(destination = "PAYMENT_SUCCESS_QUEUE",containerFactory = "jmsQueueListener")
    public void consumePaymentResultMsg(MapMessage message) throws JMSException {
        String out_trade_no = message.getString("out_trade_no");

        //更新订单状态
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(out_trade_no);
        orderService.updateOrder(omsOrder);
    }
}
