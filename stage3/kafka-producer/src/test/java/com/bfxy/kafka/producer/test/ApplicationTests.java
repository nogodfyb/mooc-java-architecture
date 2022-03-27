package com.bfxy.kafka.producer.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bfxy.kafka.producer.KafkaProducerService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private KafkaProducerService kafkaProducerService;

	@Test
	public void send() throws InterruptedException {

		String topic = "test";
		for(int i=0; i < 1000; i ++) {
			kafkaProducerService.sendMessage(topic, "hello test" + i);
			Thread.sleep(5);
		}

		Thread.sleep(Integer.MAX_VALUE);

	}

}
