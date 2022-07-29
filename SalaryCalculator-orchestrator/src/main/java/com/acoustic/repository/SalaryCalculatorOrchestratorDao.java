package com.acoustic.repository;


import com.acoustic.entity.SalaryCalculatorOrchestratorData;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface SalaryCalculatorOrchestratorDao {

    BigDecimal findAverageByJobTitle(String jonTitle);

    SalaryCalculatorOrchestratorData save(SalaryCalculatorOrchestratorData Microservices);




}
