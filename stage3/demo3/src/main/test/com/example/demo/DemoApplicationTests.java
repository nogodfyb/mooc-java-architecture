package com.example.demo;

import com.example.demo.component.Bean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DemoApplicationTests {

	@Autowired
	private Bean bean;


	@Test
	public void test(){
		bean.test();
	}



}
