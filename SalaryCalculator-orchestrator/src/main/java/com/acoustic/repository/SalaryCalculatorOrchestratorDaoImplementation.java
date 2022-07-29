package com.acoustic.repository;

import com.acoustic.entity.SalaryCalculatorOrchestratorData;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryCalculatorOrchestratorDaoImplementation implements SalaryCalculatorOrchestratorDao{

    public final DynamoDBMapper dynamoDBMapper;

    public static final String JOB_TITLE_NAME = "jobTitle";


    @Override
    public SalaryCalculatorOrchestratorData save(SalaryCalculatorOrchestratorData salaryCalculatorOrchestratorData) {
        this.dynamoDBMapper.save(salaryCalculatorOrchestratorData);
        return salaryCalculatorOrchestratorData;

    }


    private List<SalaryCalculatorOrchestratorData> findGrossSalaryByJobTitle(String  jobTitle) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.addFilterCondition(JOB_TITLE_NAME, new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(jobTitle)));
        return this.dynamoDBMapper.scan(SalaryCalculatorOrchestratorData.class, scanExpression);
    }


    public BigDecimal findAverageByJobTitle(String jobTitle){
        var monthlyGrossSalary = findGrossSalaryByJobTitle(jobTitle).stream().map(SalaryCalculatorOrchestratorData::getGrossMonthlySalary).collect(Collectors.toList());
        BigDecimal[] totalWithCount
                = monthlyGrossSalary.stream()
                .filter(Objects::nonNull)
                .map(bd -> new BigDecimal[]{bd, BigDecimal.ONE})
                .reduce((a, b) -> new BigDecimal[]{a[0].add(b[0]), a[1].add(BigDecimal.ONE)}).orElse(null);
        return Objects.requireNonNull(totalWithCount)[0].divide(totalWithCount[1], RoundingMode.HALF_EVEN);

    }

}
