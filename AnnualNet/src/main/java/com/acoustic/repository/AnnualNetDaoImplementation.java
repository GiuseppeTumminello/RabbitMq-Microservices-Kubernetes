package com.acoustic.repository;

import com.acoustic.entity.AnnualNet;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnnualNetDaoImplementation implements AnnualNetDao {

    public final DynamoDBMapper dynamoDBMapper;

    @Override
    public AnnualNet save(AnnualNet annualNet) {
        this.dynamoDBMapper.save(annualNet);
        return annualNet;
    }
}
