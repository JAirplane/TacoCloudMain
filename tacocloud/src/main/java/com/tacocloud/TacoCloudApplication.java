package com.tacocloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
//TODO
@SpringBootApplication(scanBasePackages = "com.tacocloud.*")
//@EntityScan("tacos.tacocloud_domain")
//@ComponentScan("tacos.messaging.jms")
public class TacoCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(TacoCloudApplication.class, args);
	}

}
