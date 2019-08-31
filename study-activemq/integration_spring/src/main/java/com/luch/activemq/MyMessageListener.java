package com.luch.activemq;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author luch
 * @date 2019/8/31-19:41
 */
@Component
public class MyMessageListener implements MessageListener{

    @Override
    public void onMessage(Message message) {
        if(message!=null&&message instanceof TextMessage){
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.println("****************消费者接收到消息："+textMessage.getText()+"****************");
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
