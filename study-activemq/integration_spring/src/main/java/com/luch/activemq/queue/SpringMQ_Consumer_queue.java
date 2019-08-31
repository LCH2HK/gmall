package com.luch.activemq.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * @author luch
 * @date 2019/8/31-19:17
 */
@Service
public class SpringMQ_Consumer_queue {

    @Autowired
    private JmsTemplate jmsTemplate;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        SpringMQ_Consumer_queue consumer = applicationContext.getBean(SpringMQ_Consumer_queue.class);
        String receivedMsg = (String) consumer.jmsTemplate.receiveAndConvert();
        System.out.println("******消费者受到的消息："+receivedMsg);
    }
}
