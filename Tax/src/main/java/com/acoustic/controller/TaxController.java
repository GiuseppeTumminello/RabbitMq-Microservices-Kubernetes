package com.acoustic.controller;


import com.acoustic.entity.Tax;
import com.acoustic.repository.TaxDao;
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
@RequestMapping("/tax")
@CrossOrigin
@Slf4j
public class TaxController {

    public static final int MINIMUM_GROSS = 2000;
    private final TaxDao taxDao;
    private final SalaryCalculatorService salaryCalculatorService;
    private final ObjectMapper objectMapper;
    private final static String TAX_QUEUE = "tax-queue";


    @SqsListener(TAX_QUEUE)
    public void messageReceiver(@NotificationMessage String snsMessage) {
        log.info("Message received: {}", snsMessage);
        var tax = convertJsonToTaxObject(snsMessage);
        sendTaxDataToSalaryCalculatorOrchestrator(tax.getAmount(), tax.getUuid());
    }


    @SneakyThrows
    public Tax convertJsonToTaxObject(String snsMessage) {
        return this.objectMapper.readValue(snsMessage, Tax.class);
    }


    @PostMapping("/calculation/{grossMonthlySalary}")
    public ResponseEntity<Map<String, String>> calculationTaxEndpoint(@PathVariable @Min(MINIMUM_GROSS) BigDecimal grossMonthlySalary) {
        var taxZus = calculateTax(grossMonthlySalary);
        saveTax(taxZus, UUID.randomUUID());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(this.salaryCalculatorService.getDescription(), String.valueOf(taxZus)));
    }

    private void sendTaxDataToSalaryCalculatorOrchestrator(BigDecimal grossMonthlySalary, UUID uuid) {
        var tax = calculateTax(grossMonthlySalary);
        var taxData = saveTax(tax, uuid);
        this.salaryCalculatorService.sendTax(taxData);
    }

    private Tax saveTax(BigDecimal tax, UUID uuid) {
        return this.taxDao.save(Tax.builder().description(this.salaryCalculatorService.getDescription()).amount(tax).uuid(uuid).build());
    }

    private BigDecimal calculateTax(BigDecimal grossMonthlySalary) {
        return this.salaryCalculatorService.apply(grossMonthlySalary);
    }


}
