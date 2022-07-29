package com.acoustic.controller;


import com.acoustic.entity.AnnualGross;
import com.acoustic.repository.AnnualGrossDao;
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
@RequestMapping("/annual-gross")
@RequiredArgsConstructor
@Validated
@CrossOrigin
@Slf4j
public class AnnualGrossController {

    public static final int MINIMUM_GROSS = 2000;
    private final SalaryCalculatorService salaryCalculatorService;
    private final AnnualGrossDao annualGrossDao;
    private final ObjectMapper objectMapper;
    private static final String ANNUAL_GROSS_QUEUE = "annual-gross-queue";


    @SqsListener(ANNUAL_GROSS_QUEUE)
    public void messageReceiver(@NotificationMessage String snsMessage) {
        log.info("Message received: {}", snsMessage);
        var annualGross = convertJsonToAnnualGrossObject(snsMessage);
        sendAnnualGrossDataToSalaryCalculatorOrchestrator(annualGross.getAmount(), annualGross.getUuid());
    }


    @SneakyThrows
    public AnnualGross convertJsonToAnnualGrossObject(String snsMessage) {
        return this.objectMapper.readValue(snsMessage, AnnualGross.class);
    }



    @PostMapping("/calculation/{grossMonthlySalary}")
    public ResponseEntity<Map<String, String>> calculationAnnualGrossEndpoint(@PathVariable @Min(MINIMUM_GROSS)BigDecimal grossMonthlySalary){
        var annualGross = calculateAnnualGross(grossMonthlySalary);
        saveAnnualGross(annualGross, UUID.randomUUID());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(this.salaryCalculatorService.getDescription(), String.valueOf(annualGross)));
    }

    private void sendAnnualGrossDataToSalaryCalculatorOrchestrator(BigDecimal grossMonthlySalary, UUID uuid) {
        var annualGrossSalary = calculateAnnualGross(grossMonthlySalary);
        var annualGrossData = saveAnnualGross(annualGrossSalary, uuid);
        this.salaryCalculatorService.sendAnnualGross(annualGrossData);
    }

    private AnnualGross saveAnnualGross(BigDecimal annualGross, UUID uuid) {
         return this.annualGrossDao.save(AnnualGross.builder().description(this.salaryCalculatorService.getDescription()).amount(annualGross).uuid(uuid).build());
    }

    private BigDecimal calculateAnnualGross(BigDecimal grossMonthlySalary) {
        return this.salaryCalculatorService.apply(grossMonthlySalary);
    }
}
