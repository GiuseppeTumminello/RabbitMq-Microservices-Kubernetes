package com.acoustic.controller;


import com.acoustic.entity.SicknessZus;
import com.acoustic.repository.SicknessZusDao;
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
@RequestMapping("/sickness-zus")
@RequiredArgsConstructor
@Validated
@CrossOrigin
@Slf4j
public class SicknessZusController {


    public static final int MINIMUM_GROSS = 2000;

    private final SicknessZusDao sicknessZusDao;
    private final SalaryCalculatorService salaryCalculatorService;

    private final ObjectMapper objectMapper;

    private static final String SICKNESS_ZUS_QUEUE = "sickness-zus-queue";


    @SqsListener(SICKNESS_ZUS_QUEUE)
    public void messageReceiver(@NotificationMessage String snsMessage) {
        log.info("Message received: {}", snsMessage);
        var sicknessZus = convertJsonToSicknessZusObject(snsMessage);
        sendSicknessZusDataToSalaryCalculatorOrchestrator(sicknessZus.getAmount(), sicknessZus.getUuid());
    }


    @SneakyThrows
    public SicknessZus convertJsonToSicknessZusObject(String snsMessage) {
        return this.objectMapper.readValue(snsMessage, SicknessZus.class);
    }


    @PostMapping("/calculation/{grossMonthlySalary}")
    public ResponseEntity<Map<String, String>> calculationSicknessZusEndpoint(@PathVariable @Min(MINIMUM_GROSS) BigDecimal grossMonthlySalary) {
        var sicknessZus = calculateSicknessZus(grossMonthlySalary);
        saveSicknessZus(sicknessZus, UUID.randomUUID());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(this.salaryCalculatorService.getDescription(), String.valueOf(sicknessZus)));
    }

    private void sendSicknessZusDataToSalaryCalculatorOrchestrator(BigDecimal grossMonthlySalary, UUID uuid) {
        var sicknessZus = calculateSicknessZus(grossMonthlySalary);
        var sicknessZusData = saveSicknessZus(sicknessZus, uuid);
        this.salaryCalculatorService.sendSicknessZus(sicknessZusData);
    }

    private SicknessZus saveSicknessZus(BigDecimal sicknessZus, UUID uuid) {
        return this.sicknessZusDao.save(SicknessZus.builder().description(this.salaryCalculatorService.getDescription()).amount(sicknessZus).uuid(uuid).build());
    }

    private BigDecimal calculateSicknessZus(BigDecimal grossMonthlySalary) {
        return this.salaryCalculatorService.apply(grossMonthlySalary);
    }

}
