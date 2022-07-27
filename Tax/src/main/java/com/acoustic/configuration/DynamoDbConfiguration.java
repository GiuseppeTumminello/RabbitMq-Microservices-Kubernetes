package com.acoustic.configuration;


import com.acoustic.dynamodb.DynamoDbCredential;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
public class DynamoDbConfiguration {

    private final DynamoDbCredential dynamoDbCredential;

    @Bean
    public DynamoDBMapper mapper(){
        return new DynamoDBMapper(amazonDynamoDbConfig());
    }

    public AmazonDynamoDB amazonDynamoDbConfig() {
        return AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(this.dynamoDbCredential.getServiceEndpoint(),this.dynamoDbCredential.getRegion()))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.dynamoDbCredential.getAccessKey(),this.dynamoDbCredential.getSecretKey()))).build();
    }


}
