package com.acoustic.controller;


import com.acoustic.entity.DisabilityZus;
import com.acoustic.repository.DisabilityZusDao;
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
@RequestMapping("/disability-zus")
@RequiredArgsConstructor
@Validated
@CrossOrigin
@Slf4j
public class DisabilityZusController {

    public static final int MINIMUM_GROSS = 2000;
    private final DisabilityZusDao disabilityZusDao;
    private final SalaryCalculatorService salaryCalculatorService;


    private final ObjectMapper objectMapper;

    private static final String DISABILITY_ZUS_QUEUE = "disability-zus-queue";


    @SqsListener(DISABILITY_ZUS_QUEUE)
    public void messageReceiver(@NotificationMessage String snsMessage) {
        log.info("Message received: {}", snsMessage);
        var disabilityZus = convertJsonToDisabilityZusObject(snsMessage);
        sendDisabilityZusDataToSalaryCalculatorOrchestrator(disabilityZus.getAmount(), disabilityZus.getUuid());
    }


    @SneakyThrows
    public DisabilityZus convertJsonToDisabilityZusObject(String snsMessage) {
        return this.objectMapper.readValue(snsMessage, DisabilityZus.class);
    }





    @PostMapping("/calculation/{grossMonthlySalary}")
    public ResponseEntity<Map<String, String>> calculationDisabilityZusEndpoint(@PathVariable @Min(MINIMUM_GROSS) BigDecimal grossMonthlySalary) {
        var disabilityZus = calculateDisabilityZus(grossMonthlySalary);
        saveDisabilityZus(disabilityZus, UUID.randomUUID());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(this.salaryCalculatorService.getDescription(), String.valueOf(disabilityZus)));
    }

    private void sendDisabilityZusDataToSalaryCalculatorOrchestrator(BigDecimal grossMonthlySalary, UUID uuid) {
        var disabilityZus = calculateDisabilityZus(grossMonthlySalary);
        var disabilityZusData = saveDisabilityZus(disabilityZus, uuid);
        this.salaryCalculatorService.sendDisabilityZus(disabilityZusData);
    }

    private DisabilityZus saveDisabilityZus(BigDecimal disabilityZus, UUID uuid) {
        return this.disabilityZusDao.save(DisabilityZus.builder().description(this.salaryCalculatorService.getDescription()).amount(disabilityZus).uuid(uuid).build());
    }

    private BigDecimal calculateDisabilityZus(BigDecimal grossMonthlySalary) {
        return this.salaryCalculatorService.apply(grossMonthlySalary);
    }
}
