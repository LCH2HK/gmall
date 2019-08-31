package com.luch.gmall.payment.mq;

import com.luch.gmall.bean.PaymentInfo;
import com.luch.gmall.service.PaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Date;
import java.util.Map;

/**
 * @author luch
 * @date 2019/8/31-0:00
 */
@Component
public class PaymentServiceMqListener {

    @Autowired
    PaymentService paymentService;

    @JmsListener(destination = "PAYMENT_CHECK_QUEUE",containerFactory = "jmsQueueListener")
    public void consumePaymentCheckResult(MapMessage message) throws JMSException {
        String out_trade_no = message.getString("out_trade_no");
        Integer count=0;
        if(message.getString("count")!=null){
            count=Integer.parseInt(""+message.getString("count"));
        }

        //调用paymentService的支付宝检查接口
        Map<String,Object> result=paymentService.checkAlipayPayment(out_trade_no);

        if(result!=null&&!result.isEmpty()){
            String trade_status = (String) result.get("trade_status");
            //根据查询的支付状态结果，判断是否进行下一次的延迟任务还是支付成功更新数据和后序任务
            if(StringUtils.isNotBlank(trade_status)&&trade_status.equals("TRADE_SUCCESS")){
                //支付成功，更新支付发送支付队列
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setOrderSn(out_trade_no);
                paymentInfo.setPaymentStatus("已支付");
                paymentInfo.setAlipayTradeNo((String)result.get("trade_no"));//支付宝的交易凭证号
                paymentInfo.setCallbackContent((String)result.get("call_back_content"));
                paymentInfo.setCallbackTime(new Date());

                System.out.println("已经支付成功，调用支付服务，修改支付信息和发送支付成功的队列");
                paymentService.updatePayment(paymentInfo);
                return;
            }
        }

        if(count>0){
            //继续发送延迟检查任务，计算延迟时间等
            System.out.println("没有支付成功，检查剩余次数为"+count+",继续发送延迟检查任务");
            count--;
            paymentService.sendPaymentResultCheckDelayQueue(out_trade_no,count);

        }else{
            System.out.println("检查剩余次数用尽，结束检查");
        }
    }
}
