package com.acoustic.repository;

import com.acoustic.entity.MonthlyGross;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MonthlyGrossDaoImplementation implements MonthlyGrossDao {

    public final DynamoDBMapper dynamoDBMapper;

    @Override
    public MonthlyGross save(MonthlyGross monthlyGross) {
        this.dynamoDBMapper.save(monthlyGross);
        return monthlyGross;
    }
}
