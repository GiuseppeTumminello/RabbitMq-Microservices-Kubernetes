package com.acoustic.controller;


import com.acoustic.entity.MonthlyNet;
import com.acoustic.repository.MonthlyNetDao;
import com.acoustic.service.SalaryCalculatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/monthly-net")
@CrossOrigin
@Slf4j
public class MonthlyNetController {

    public static final int MINIMUM_GROSS = 2000;
    private final MonthlyNetDao monthlyNetDao;
    private final SalaryCalculatorService salaryCalculatorService;
    private final ObjectMapper objectMapper;

    private static final String MONTHLY_NET_QUEUE = "monthly-net-queue";


    @SqsListener(MONTHLY_NET_QUEUE)
    public void messageReceiver(@NotificationMessage String snsMessage) {
        log.info("Message received: {}", snsMessage);
        var monthlyNet = convertJsonToMonthlyNetObject(snsMessage);
        sendMonthlyNetDataToSalaryCalculatorOrchestrator(monthlyNet.getAmount(), monthlyNet.getUuid());
    }


    @SneakyThrows
    public MonthlyNet convertJsonToMonthlyNetObject(String snsMessage) {
        return this.objectMapper.readValue(snsMessage, MonthlyNet.class);
    }


    @PostMapping("/calculation/{grossMonthlySalary}")
    public ResponseEntity<Map<String, String>> calculationMonthlyNetEndpoint(@PathVariable @Min(MINIMUM_GROSS) BigDecimal grossMonthlySalary) {
        var annualNet = calculateMonthlyNet(grossMonthlySalary);
        saveMonthlyNetData(annualNet, UUID.randomUUID());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(this.salaryCalculatorService.getDescription(), String.valueOf(annualNet)));
    }

    private void sendMonthlyNetDataToSalaryCalculatorOrchestrator(BigDecimal grossMonthlySalary, UUID uuid) {
        var monthlyNet = calculateMonthlyNet(grossMonthlySalary);
        var monthlyNetData = saveMonthlyNetData(monthlyNet, uuid);
        this.salaryCalculatorService.sendMonthlyNet(monthlyNetData);
    }

    private MonthlyNet saveMonthlyNetData(BigDecimal monthlyNet, UUID uuid) {
        return this.monthlyNetDao.save(MonthlyNet.builder().description(this.salaryCalculatorService.getDescription()).amount(monthlyNet).uuid(uuid).build());
    }

    private BigDecimal calculateMonthlyNet(BigDecimal grossMonthlySalary) {
        return this.salaryCalculatorService.apply(grossMonthlySalary);
    }


}
