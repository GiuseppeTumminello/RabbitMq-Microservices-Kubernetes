package com.acoustic.configuartion;


import com.acoustic.awssettings.AwsSettings;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AwsSqsConfiguration  {

    private final AwsSettings awsSettings;

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(){
        return new QueueMessagingTemplate(awsSqsAsync());
    }

    public AmazonSQSAsync awsSqsAsync() {
        return AmazonSQSAsyncClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new ProfileCredentialsProvider(this.awsSettings.getProfileName())).build();
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory() {
        SimpleMessageListenerContainerFactory msgListenerContainerFactory = new SimpleMessageListenerContainerFactory();
        msgListenerContainerFactory.setAmazonSqs(awsSqsAsync());
        return msgListenerContainerFactory;
    }




}
