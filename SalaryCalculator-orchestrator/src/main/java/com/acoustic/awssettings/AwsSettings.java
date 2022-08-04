package com.acoustic.awssettings;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Configuration
@PropertySource("classpath:aws.properties")
@ConfigurationProperties(prefix = "aws")
@Component
public class AwsSettings {

    private String arnSalaryCalculatorTopic;
    private String profileName;
    private String sqsEndpoint;
    private String queueUrl;
}
