package com.acoustic.service;

import com.acoustic.awssettings.AwsSettings;
import com.acoustic.entity.DisabilityZus;
import com.acoustic.rate.RatesConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class DisabilityZuzService implements SalaryCalculatorService{
    private final RatesConfigurationProperties ratesConfigurationProperties;
    private final QueueMessagingTemplate queueMessagingTemplate;
    private final AwsSettings awsSettings;

    @Override
    public String getDescription() {
        return "Disability zus";
    }

    @Override
    public void sendDisabilityZus(DisabilityZus disabilityZus) {
        this.queueMessagingTemplate.convertAndSend(this.awsSettings.getSqsEndpoint(), disabilityZus);
    }

    @Override
    public BigDecimal apply(final BigDecimal grossMonthlySalary) {
        return grossMonthlySalary.multiply(this.ratesConfigurationProperties.getDisabilityZusRate()).setScale(2, RoundingMode.HALF_EVEN);
    }
}
