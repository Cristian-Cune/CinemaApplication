package com.idp.cinema.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cinema")
public class CinemaProperties {
    private String queue;
    private String adminQueue;
    private String exchange;
    private String routingKey;
    private String adminRoutingKey;
}
