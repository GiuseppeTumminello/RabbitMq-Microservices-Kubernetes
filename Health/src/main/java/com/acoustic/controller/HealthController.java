package com.acoustic.controller;


import com.acoustic.entity.Health;
import com.acoustic.repository.HealthZusDao;
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
@RequestMapping("/health")
@CrossOrigin
@Slf4j
public class HealthController {
    public static final int MINIMUM_GROSS = 2000;
    private final HealthZusDao healthZusDao;
    private final SalaryCalculatorService salaryCalculatorService;

    private final ObjectMapper objectMapper;

    private static final String HEALTH_QUEUE = "health-queue";


    @SqsListener(HEALTH_QUEUE)
    public void messageReceiver(@NotificationMessage String snsMessage) {
        log.info("Message received: {}", snsMessage);
        var health = convertJsonToHealthObject(snsMessage);
        sendHealthDataToSalaryCalculatorOrchestrator(health.getAmount(), health.getUuid());
    }


    @SneakyThrows
    public Health convertJsonToHealthObject(String snsMessage) {
        return this.objectMapper.readValue(snsMessage, Health.class);
    }


    @PostMapping("/calculation/{grossMonthlySalary}")
    public ResponseEntity<Map<String, String>> calculationHealthEndpoint(@PathVariable @Min(MINIMUM_GROSS) BigDecimal grossMonthlySalary) {
        var health = calculateHealth(grossMonthlySalary);
        saveHealth(health, UUID.randomUUID());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(this.salaryCalculatorService.getDescription(), String.valueOf(health)));
    }

    private void sendHealthDataToSalaryCalculatorOrchestrator(BigDecimal grossMonthlySalary, UUID uuid) {
        var health = calculateHealth(grossMonthlySalary);
        var healthData = saveHealth(health, uuid);
        this.salaryCalculatorService.sendHealth(healthData);
    }

    private Health saveHealth(BigDecimal health, UUID uuid) {
        return this.healthZusDao.save(Health.builder().description(this.salaryCalculatorService.getDescription()).amount(health).uuid(uuid).build());
    }

    private BigDecimal calculateHealth(BigDecimal grossMonthlySalary) {
        return this.salaryCalculatorService.apply(grossMonthlySalary);
    }
}
