package com.luch.activemq.topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * @author luch
 * @date 2019/8/31-19:17
 */
@Service
public class SpringMQ_Consumer_topic {

    @Autowired
    private JmsTemplate jmsTemplate1;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        SpringMQ_Consumer_topic consumer = applicationContext.getBean(SpringMQ_Consumer_topic.class);
        String receivedMsg = (String) consumer.jmsTemplate1.receiveAndConvert();
        System.out.println("******消费者受到的消息："+receivedMsg);
    }
}
