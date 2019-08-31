package com.luch.boot.activemq.topic;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * @author luch
 * @date 2019/8/31-21:28
 */
@Component
public class Topic_Consumer {

    @JmsListener(destination = "${mytopic}")
    public void receive(TextMessage textMessage) throws JMSException{
        System.out.println("******消费者受到订阅的主题："+textMessage.getText());
    }
}
