package com.luch.boot.activemq.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import java.util.UUID;

/**
 * @author luch
 * @date 2019/8/31-20:32
 */
//@Component
public class Queue_Producer {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue queue;

    public void produceMsg(){
        jmsMessagingTemplate.convertAndSend(queue,"******:"+ UUID.randomUUID().toString().substring(0,6));
    }

    //间隔时间为3秒的定时投递
    @Scheduled(fixedDelay = 6000)
    public void produceMsgScheduled(){
        jmsMessagingTemplate.convertAndSend(queue,"******:"+ UUID.randomUUID().toString().substring(0,6));
        System.out.println("队列消息发送完毕");
    }
}
