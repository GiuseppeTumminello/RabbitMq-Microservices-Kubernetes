package com.acoustic.service;

import com.acoustic.awssettings.AwsSettings;
import com.acoustic.entity.MonthlyGross;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class MonthlyGrossService implements SalaryCalculatorService {

   private final QueueMessagingTemplate queueMessagingTemplate;

   private final AwsSettings awsSettings;


    @Override
    public String getDescription() {
        return "Monthly gross";
    }

    @Override
    public void sendMonthlyGross(MonthlyGross monthlyGross) {
        this.queueMessagingTemplate.convertAndSend(this.awsSettings.getSqsEndpoint(), monthlyGross);
    }

    @Override
    public BigDecimal apply(final BigDecimal grossMonthlySalary) {
        return grossMonthlySalary.setScale(2, RoundingMode.HALF_EVEN);
    }
}
