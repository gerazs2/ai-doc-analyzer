package com.jgmagalop.aidocanalyzer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class AiDocAnalyzerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void generarHash() {
		System.out.println(new BCryptPasswordEncoder().encode("Admin123!"));
	}

}
