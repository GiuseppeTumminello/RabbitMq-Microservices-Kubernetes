package com.acoustic.repository;

import com.acoustic.entity.MonthlyNet;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MonthlyNetDaoImplementation implements MonthlyNetDao {

    public final DynamoDBMapper dynamoDBMapper;

    @Override
    public MonthlyNet save(MonthlyNet monthlyNet) {
        this.dynamoDBMapper.save(monthlyNet);
        return monthlyNet;
    }
}
