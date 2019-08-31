package com.luch.boot.activemq;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.jms.Queue;
import javax.jms.Topic;

@SpringBootApplication
@EnableJms
@EnableScheduling //开启定时功能
public class BootMqProducerApplication {

	@Value("${myqueue}")
	private String myQueue;

	@Value("${mytopic}")
	private String myTopic;

	@Bean
	public Queue queue(){
		return new ActiveMQQueue(myQueue);
	}

	@Bean
	public Topic topic(){
		return new ActiveMQTopic(myTopic);
	}

	public static void main(String[] args) {

		SpringApplication.run(BootMqProducerApplication.class, args);
	}

}
