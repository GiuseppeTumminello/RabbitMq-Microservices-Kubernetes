package com.acoustic.controller;


import com.acoustic.entity.PensionZus;
import com.acoustic.repository.PensionZusDao;
import com.acoustic.service.SalaryCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
