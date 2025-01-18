package com.tacocloud;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Configuration
@Profile("!prod")
public class DevelopmentConfig {
}
