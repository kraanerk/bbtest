package com.bbtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoadsAndContainsExpectedBeans() {
		assertNotNull(applicationContext.getBean(RestTemplate.class));
		assertNotNull(applicationContext.getBean(GameController.class));
	}

}
