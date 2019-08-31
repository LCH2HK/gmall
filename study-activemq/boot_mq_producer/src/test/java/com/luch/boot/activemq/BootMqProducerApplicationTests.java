package com.luch.boot.activemq;

import com.luch.boot.activemq.queue.Queue_Producer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BootMqProducerApplicationTests {

	@Resource
	private Queue_Producer queue_producer;



	@Test
	public void contextLoads() {
		queue_producer.produceMsgScheduled();
	}

}
