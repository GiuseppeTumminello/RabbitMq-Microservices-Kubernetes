package com.acoustic.controller;


import com.acoustic.entity.PensionZus;
import com.acoustic.repository.PensionZusDao;
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
@RequestMapping("/pension-zus")
@RequiredArgsConstructor
@Validated
@CrossOrigin
@Slf4j
public class PensionZusController {

    public static final int MINIMUM_SALARY = 2000;
    private final PensionZusDao pensionZusDao;
    private final SalaryCalculatorService salaryCalculatorService;

    private final ObjectMapper objectMapper;

    private static final String PENSION_ZUS_QUEUE = "pension-zus-queue";


    @SqsListener(PENSION_ZUS_QUEUE)
    public void messageReceiver(@NotificationMessage String snsMessage) {
        log.info("Message received: {}", snsMessage);
        var pensionZus = convertJsonToPensionZusObject(snsMessage);
        sendPensionZusDataToSalaryCalculatorOrchestrator(pensionZus.getAmount(), pensionZus.getUuid());
    }


    @SneakyThrows
    public PensionZus convertJsonToPensionZusObject(String snsMessage) {
        return this.objectMapper.readValue(snsMessage, PensionZus.class);
    }




    @PostMapping("/calculation/{grossMonthlySalary}")
    public ResponseEntity<Map<String, String>> calculationPensionZusEndpoint(@PathVariable @Min(MINIMUM_SALARY) BigDecimal grossMonthlySalary) {
        var pensionZus = calculatePensionZusData(grossMonthlySalary);
        savePensionZus(pensionZus, UUID.randomUUID());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(this.salaryCalculatorService.getDescription(), String.valueOf(pensionZus)));
    }

    private void sendPensionZusDataToSalaryCalculatorOrchestrator(BigDecimal grossMonthlySalary, UUID uuid) {
        var pensionZus = calculatePensionZusData(grossMonthlySalary);
        var pensionZusData = savePensionZus(pensionZus, uuid);
        this.salaryCalculatorService.sendPensionZus(pensionZusData);
    }

    private PensionZus savePensionZus(BigDecimal pensionZus, UUID uuid) {
        return this.pensionZusDao.save(PensionZus.builder().description(this.salaryCalculatorService.getDescription()).amount(pensionZus).uuid(uuid).build());
    }

    private BigDecimal calculatePensionZusData(BigDecimal grossMonthlySalary) {
        return this.salaryCalculatorService.apply(grossMonthlySalary);
    }

}
