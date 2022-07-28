package com.acoustic.controller;


import com.acoustic.entity.AnnualNet;
import com.acoustic.repository.AnnualNetDao;
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
@RequestMapping("/annual-net")
@RequiredArgsConstructor
@Validated
@CrossOrigin
@Slf4j
public class AnnualNetController{
    public static final int MINIMUM_GROSS = 2000;
    private final AnnualNetDao annualNetRepository;
    private final SalaryCalculatorService salaryCalculatorService;

    @PostMapping("/calculation/{grossMonthlySalary}")
    public ResponseEntity<Map<String, String>> calculationAnnualNetEndpoint(@PathVariable @Min(MINIMUM_GROSS)BigDecimal grossMonthlySalary){
        var annualNet = calculateAnnualNet(grossMonthlySalary);
        saveAnnualNet(annualNet, UUID.randomUUID());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(this.salaryCalculatorService.getDescription(), String.valueOf(annualNet)));
    }

    private void sendAnnualNetEndpointDataToSalaryCalculatorOrchestrator(BigDecimal grossMonthlySalary, UUID uuid) {
        var annualNetSalary = calculateAnnualNet(grossMonthlySalary);
        var annualNetData = saveAnnualNet(annualNetSalary, uuid);
        this.salaryCalculatorService.sendAnnualNet(annualNetData);
    }

    private AnnualNet saveAnnualNet(BigDecimal annualNet, UUID uuid) {
        return this.annualNetRepository.save(AnnualNet.builder().description(this.salaryCalculatorService.getDescription()).amount(annualNet).uuid(uuid).build());
    }

    private BigDecimal calculateAnnualNet(BigDecimal grossMonthlySalary) {
        return this.salaryCalculatorService.apply(grossMonthlySalary);
    }

}
