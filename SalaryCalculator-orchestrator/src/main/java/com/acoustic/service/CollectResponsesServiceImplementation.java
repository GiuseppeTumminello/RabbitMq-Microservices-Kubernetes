package com.acoustic.service;

import com.acoustic.awssettings.AwsSettings;
import com.acoustic.entity.MicroservicesData;
import com.acoustic.repository.MicroservicesDataDao;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CollectResponsesServiceImplementation implements CollectResponsesService{

    public static final int NUMBER_OF_CHECKS = 50;
    public static final int MICROSERVICE_COUNT = 10;

    private final MicroservicesDataDao microservicesDataDao;
    private final AmazonSNSClient amazonSNSClient;

    private final ObjectMapper objectMapper;

    private final AwsSettings awsSettings;

    @Override
    @SneakyThrows
    public void sendDataToMicroservices(BigDecimal grossMonthlySalary, UUID uuid) {
        this.amazonSNSClient.publish(this.awsSettings.getArnSalaryCalculatorTopic(), this.objectMapper.writeValueAsString(MicroservicesData.builder().amount(grossMonthlySalary).uuid(uuid).description(this.getClass().getSimpleName()).id("random").build()), "ciao");
    }


    @Override
    public Map<String, BigDecimal> collectMicroservicesResponse(UUID uuid) {
        Map<String, BigDecimal> response = new HashMap<>();
        List<MicroservicesData> data = new ArrayList<>();
        var count = 0;
        while (data.size() < MICROSERVICE_COUNT) {
            data = this.microservicesDataDao.findByUuid(String.valueOf(uuid));
            count++;
            if (count == NUMBER_OF_CHECKS) {
                break;
            }
        }
        data.forEach(microservicesData -> response.put(microservicesData.getDescription(), microservicesData.getAmount()));
        return response;
    }
}
