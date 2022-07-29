package com.acoustic.configuartion;

import com.acoustic.awssettings.AwsSettings;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class AwsSnsConfiguration {

    private final AwsSettings awsSettings;
    @Primary
    @Bean
    public AmazonSNSClient amazonSNSClient (){
        return (AmazonSNSClient) AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new ProfileCredentialsProvider(this.awsSettings.getProfileName())).build();
    }


}
