package com.acoustic.sqstest;

import com.acoustic.entity.AnnualGross;
import com.acoustic.repository.AnnualGrossDao;
import com.acoustic.service.SalaryCalculatorService;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;


@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class SqsTest {

    @TestConfiguration
    static class AwsTestConfig {



        @Bean
        public AmazonSQSAsync amazonSQS() {
            return AmazonSQSAsyncClientBuilder.standard()
                    .withCredentials(localStack.getDefaultCredentialsProvider())
                    .withEndpointConfiguration(localStack.getEndpointConfiguration(SQS))
                    .build();
        }
    }

    @Container
    static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14.3"))
                    .withClasspathResourceMapping("/localstack", "/docker-entrypoint-initaws.d", BindMode.READ_ONLY)
                    .withServices(SQS)
                    .waitingFor(Wait.forLogMessage(".*Initialized\\.\n", 1));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("cloud.aws.sqs.endpoint", () -> localStack.getEndpointOverride(SQS).toString());
        registry.add("cloud.aws.credentials.access-key", () -> "foo");
        registry.add("cloud.aws.credentials.secret-key", () -> "bar");
        registry.add("cloud.aws.region.static", () -> localStack.getRegion());
        registry.add("order-queue-name", () -> "test-order-queue");
    }



    @Autowired
    private  AwsTestConfig awsSqsConfiguration;

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    private SalaryCalculatorService salaryCalculatorService;

    @MockBean
    private AnnualGrossDao annualGrossDao;

    @Test
    void shouldStoreIncomingPurchaseOrderInDatabase() {
        System.out.println(localStack.getEndpointOverride(SQS).toString());
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest("annual-gross-queue").withMaxNumberOfMessages(10);
        var body = AnnualGross.builder().description(this.salaryCalculatorService.getDescription()).amount(BigDecimal.valueOf(6000)).uuid(UUID.randomUUID()).build();
        Map<String, Object> messageHeaders = Map.of("contentType", "application/json");
        queueMessagingTemplate
                .convertAndSend("test-order-queue", body);

        //var messageList = awsSqsConfiguration.amazonSQS().receiveMessage(receiveMessageRequest).getMessages();
        //System.out.println(messageList.toString());

        //given(this.annualGrossDao.save(any())).willReturn(body);
       // assertThat(body.toString())
                //.isEqualTo(messageList.get(0).toString());
    }


    }




