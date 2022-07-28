package com.acoustic.service;


import com.acoustic.awssettings.AwsSettings;
import com.acoustic.entity.AnnualGross;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class AnnualGrossAmount implements SalaryCalculatorService {
    public static final int MONTHS_NUMBER = 12;

    private final RabbitTemplate rabbitTemplate;

    private final AwsSettings awsSettings;

    @Override
    public String getDescription() {
        return "Annual gross";
    }

    @Override
    public void sendAnnualGross(AnnualGross annualGross) {
        this.rabbitTemplate.convertAndSend(this.awsSettings.getSqsEndpoint(), annualGross);
    }


    @Override
    public BigDecimal apply(final BigDecimal grossMonthlySalary) {
        return grossMonthlySalary.multiply(BigDecimal.valueOf(MONTHS_NUMBER)).setScale(2, RoundingMode.HALF_EVEN);
    }


}
