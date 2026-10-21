package com.qldatn.dangky.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WebConfig {

    @Bean
    @LoadBalanced // Kích hoạt Dynamic Service Resolution (Tự động đổi AUTH-SERVICE thành IP/Port)
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}