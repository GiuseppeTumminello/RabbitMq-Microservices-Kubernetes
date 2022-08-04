package com.acoustic.service;

import com.acoustic.awssettings.AwsSettings;
import com.acoustic.entity.MonthlyNet;
import com.acoustic.rate.RatesConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class MonthlyNetService implements SalaryCalculatorService {

    private static final int MONTHS_NUMBER = 12;
    private final RatesConfigurationProperties rate;
    private final QueueMessagingTemplate queueMessagingTemplate;

    private final AwsSettings awsSettings;


    @Override
    public BigDecimal apply(BigDecimal grossMonthlySalary) {
        if (grossMonthlySalary.multiply(BigDecimal.valueOf(MONTHS_NUMBER)).compareTo(this.rate.getTaxGrossAmountThreshold()) < 0) {
            return getMonthlyNetBasedOnRate(grossMonthlySalary, this.rate.getTaxRate17Rate());
        } else {
            return getMonthlyNetBasedOnRate(grossMonthlySalary, this.rate.getTaxRate32Rate());
        }
    }

    @Override
    public String getDescription() {
        return "Monthly net";
    }

    @Override
    public void sendMonthlyNet(MonthlyNet monthlyNet) {
        this.queueMessagingTemplate.convertAndSend("https://sqs.us-east-1.amazonaws.com/342003767516/salary-calculator-queue", monthlyNet);
    }

    private BigDecimal calculateTotalZus(BigDecimal grossMonthlySalary) {
        return grossMonthlySalary.subtract(grossMonthlySalary.multiply(this.rate.getTotalZusRate()));
    }

    private BigDecimal calculateHealth(BigDecimal grossMonthlySalary) {
        return grossMonthlySalary.subtract(grossMonthlySalary.multiply(this.rate.getHealthRate()));
    }

    private BigDecimal getMonthlyNetBasedOnRate(BigDecimal grossMonthlySalary, BigDecimal rate) {
        var salaryMinusTotalZus = calculateTotalZus(grossMonthlySalary);
        var salaryMinusHealth = calculateHealth(salaryMinusTotalZus);
        return salaryMinusHealth.subtract(salaryMinusHealth.multiply(rate)).setScale(2, RoundingMode.HALF_EVEN);

    }

}
