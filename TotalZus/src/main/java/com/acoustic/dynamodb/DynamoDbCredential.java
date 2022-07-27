package com.acoustic.dynamodb;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@PropertySource("classpath:dynamodb.properties")
@ConfigurationProperties(prefix = "dynamodb")
public class DynamoDbCredential {

    private String serviceEndpoint;
    private String accessKey;
    private String secretKey;
    private String region;

}
