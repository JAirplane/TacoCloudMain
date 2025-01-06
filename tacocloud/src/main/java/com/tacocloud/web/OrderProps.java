package com.tacocloud.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "taco.orders")
@Data
@Validated
public class OrderProps {

    @Min(value = 5, message = "Page size must be at least 5 characters long")
    @Max(value = 25, message = "Page size must be at most 25 characters long")
    private int pageSize = 20;
}
