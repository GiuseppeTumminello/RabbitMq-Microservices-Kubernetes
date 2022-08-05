package com.acoustic.controller;


import com.acoustic.entity.TotalZus;
import com.acoustic.repository.TotalZusDao;
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
@RequestMapping("/total-zus")
@RequiredArgsConstructor
@Validated
@CrossOrigin
@Slf4j
public class TotalZusController {

    public static final int MINIMUM_GROSS = 2000;
    private final TotalZusDao totalZusDao;
    private final SalaryCalculatorService salaryCalculatorService;
    private final ObjectMapper objectMapper;
    private final static String TOTAL_ZUS_QUEUE = "total-zus-queue";


    @SqsListener(TOTAL_ZUS_QUEUE)
    public void messageReceiver(@NotificationMessage String snsMessage) {
        log.info("Message received: {}", snsMessage);
        var totalZus = convertJsonToTotalZusObject(snsMessage);
        sendTotalZusDataToSalaryCalculatorOrchestrator(totalZus.getAmount(), totalZus.getUuid());
    }


    @SneakyThrows
    public TotalZus convertJsonToTotalZusObject(String snsMessage) {
        return this.objectMapper.readValue(snsMessage, TotalZus.class);
    }


    @PostMapping("/calculation/{grossMonthlySalary}")
    public ResponseEntity<Map<String, String>> calculationTotalZusEndpoint(@PathVariable @Min(MINIMUM_GROSS) BigDecimal grossMonthlySalary) {
        var totalZus = calculateTotalZus(grossMonthlySalary);
        sendTotalZusDataToSalaryCalculatorOrchestrator(grossMonthlySalary, UUID.randomUUID());
        saveTotalZus(totalZus, UUID.randomUUID());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(this.salaryCalculatorService.getDescription(), String.valueOf(totalZus)));
    }

    private void sendTotalZusDataToSalaryCalculatorOrchestrator(BigDecimal grossMonthlySalary, UUID uuid) {
        var totalZus = calculateTotalZus(grossMonthlySalary);
        var totalZusData = saveTotalZus(totalZus, uuid);
        this.salaryCalculatorService.sendTotalZus(totalZusData);
    }

    private TotalZus saveTotalZus(BigDecimal totalZus, UUID uuid) {
        return this.totalZusDao.save(TotalZus.builder().description(this.salaryCalculatorService.getDescription()).amount(totalZus).uuid(uuid).build());
    }

    private BigDecimal calculateTotalZus(BigDecimal grossMonthlySalary) {
        return this.salaryCalculatorService.apply(grossMonthlySalary);
    }


}
