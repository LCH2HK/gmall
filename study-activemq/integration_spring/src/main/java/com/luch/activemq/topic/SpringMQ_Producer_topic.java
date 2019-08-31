package com.luch.activemq.topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.TextMessage;

/**
 * @author luch
 * @date 2019/8/31-19:09
 */
@Service
public class SpringMQ_Producer_topic {

    @Autowired
    private JmsTemplate jmsTemplate1;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        SpringMQ_Producer_topic producer = applicationContext.getBean(SpringMQ_Producer_topic.class);
        producer.jmsTemplate1.send((session -> {
            TextMessage textMessage = session.createTextMessage("Spring和ActiveMQ的整合case:topic");
            return textMessage;
        }));
        System.out.println("******发送完成******");
    }
}
