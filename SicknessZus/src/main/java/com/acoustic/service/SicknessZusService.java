package com.acoustic.service;

import com.acoustic.awssettings.AwsSettings;
import com.acoustic.entity.SicknessZus;
import com.acoustic.rate.RatesConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class SicknessZusService implements SalaryCalculatorService{

    private final RatesConfigurationProperties ratesConfigurationProperties;

    private final AwsSettings awsSettings;
    private final QueueMessagingTemplate queueMessagingTemplate;



    @Override
    public String getDescription() {
        return "Sickness zus";
    }

    @Override
    public void sendSicknessZus(SicknessZus sicknessZus) {
        this.queueMessagingTemplate.convertAndSend(this.awsSettings.getSqsEndpoint(), sicknessZus);
    }

    @Override
    public BigDecimal apply(final BigDecimal grossMonthlySalary) {
        return grossMonthlySalary.multiply(this.ratesConfigurationProperties.getSicknessZusRate()).setScale(2, RoundingMode.HALF_EVEN);
    }
}
