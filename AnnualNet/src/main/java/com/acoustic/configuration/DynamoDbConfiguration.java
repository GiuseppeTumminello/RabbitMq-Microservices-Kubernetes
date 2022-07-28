package com.acoustic.configuration;


import com.acoustic.dynamodb.DynamoDbCredential;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DynamoDbConfiguration {

    private final DynamoDbCredential dynamoDbCredential;

    @Bean
    public DynamoDBMapper mapper(){
        return new DynamoDBMapper(amazonDynamoDbConfig());
    }

    public AmazonDynamoDB amazonDynamoDbConfig() {
        return AmazonDynamoDBClientBuilder.standard().withCredentials(new ProfileCredentialsProvider(this.dynamoDbCredential.getProfileName())).withRegion(Regions.US_EAST_1).build();
    }
}
