package com.cbt.logisticservicecbtaug23one;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig
{

    @Bean
    public WebClient.Builder loadBalancedWebClientBuilder()
    {
        return WebClient.builder();
    }


}
