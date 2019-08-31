package com.luch.boot.activemq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
public class BootMqConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootMqConsumerApplication.class, args);
	}

}
