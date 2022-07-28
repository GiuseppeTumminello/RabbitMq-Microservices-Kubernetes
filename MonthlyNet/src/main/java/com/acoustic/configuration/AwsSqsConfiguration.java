package com.acoustic.configuration;


import com.acoustic.dynamodb.DynamoDbCredential;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AwsSqsConfiguration {


    private final DynamoDbCredential dynamoDbCredential;

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(){
        return new QueueMessagingTemplate(awsSqsAsync());
    }

    private AmazonSQSAsync awsSqsAsync() {
        return AmazonSQSAsyncClientBuilder.standard().withCredentials(new ProfileCredentialsProvider(this.dynamoDbCredential.getProfileName())).withRegion(Regions.US_EAST_1).build();
    }

}
