package com.acoustic.service;

import com.acoustic.awssettings.AwsSettings;
import com.acoustic.entity.PensionZus;
import com.acoustic.rate.RatesConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PensionZusService implements SalaryCalculatorService{

    private final RatesConfigurationProperties ratesConfigurationProperties;


    private final QueueMessagingTemplate queueMessagingTemplate;

    private final AwsSettings awsSettings;


    @Override
    public String getDescription() {
        return "Pension zus";
    }

    @Override
    public void sendPensionZus(PensionZus pensionZus) {
        this.queueMessagingTemplate.convertAndSend(this.awsSettings.getSqsEndpoint(), pensionZus);

    }

    @Override
    public BigDecimal apply(final BigDecimal grossMonthlySalary) {
        return grossMonthlySalary.multiply(this.ratesConfigurationProperties.getPensionZusRate()).setScale(2, RoundingMode.HALF_EVEN);
    }
}
