package com.acoustic.controller;


import com.acoustic.entity.MonthlyGross;
import com.acoustic.repository.MonthlyGrossDao;
import com.acoustic.service.SalaryCalculatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/monthly-gross")
@RequiredArgsConstructor
@Validated
@CrossOrigin
@Slf4j
public class MonthlyGrossController {

    public static final int MINIMUM_GROSS = 2000;
    private final MonthlyGrossDao monthlyGrossDao;
    private final SalaryCalculatorService salaryCalculatorService;

    private final ObjectMapper objectMapper;

    private static final String MONTHLY_GROSS_QUEUE = "monthly-gross-queue";


    @SqsListener(MONTHLY_GROSS_QUEUE)
    public void messageReceiver(@NotificationMessage String snsMessage) {
        log.info("Message received: {}", snsMessage);
        var monthlyGross = convertJsonToMonthlyGrossObject(snsMessage);
        sendMonthlyGrossDataToSalaryCalculatorOrchestrator(monthlyGross.getAmount(), monthlyGross.getUuid());
    }


    @SneakyThrows
    public MonthlyGross convertJsonToMonthlyGrossObject(String snsMessage) {
        return this.objectMapper.readValue(snsMessage, MonthlyGross.class);
    }




    @PostMapping("/calculation/{grossMonthlySalary}")
    public ResponseEntity<Map<String, String>> calculationMonthlyGrossEndpoint(@PathVariable @Min(MINIMUM_GROSS) BigDecimal grossMonthlySalary) {
        var annualNet = calculateMonthlyGross(grossMonthlySalary);
        saveMonthlyGross(annualNet, UUID.randomUUID());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(this.salaryCalculatorService.getDescription(), String.valueOf(annualNet)));
    }

    private void sendMonthlyGrossDataToSalaryCalculatorOrchestrator(BigDecimal grossMonthlySalary, UUID uuid) {
        var monthlyGross = calculateMonthlyGross(grossMonthlySalary);
        var monthlyGrossData = saveMonthlyGross(monthlyGross, uuid);
        this.salaryCalculatorService.sendMonthlyGross(monthlyGrossData);
    }

    private MonthlyGross saveMonthlyGross(BigDecimal monthlyGross, UUID uuid) {
        return this.monthlyGrossDao.save(MonthlyGross.builder().description(this.salaryCalculatorService.getDescription()).amount(monthlyGross).uuid(uuid).build());
    }

    private BigDecimal calculateMonthlyGross(BigDecimal grossMonthlySalary) {
        return this.salaryCalculatorService.apply(grossMonthlySalary);
    }
}
