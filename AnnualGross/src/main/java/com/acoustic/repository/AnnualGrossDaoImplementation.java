package com.acoustic.repository;

import com.acoustic.entity.AnnualGross;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnnualGrossDaoImplementation implements AnnualGrossDao {

    public final DynamoDBMapper dynamoDBMapper;
    @Override
    public AnnualGross save(AnnualGross annualGross) {
        this.dynamoDBMapper.save(annualGross);
        return annualGross;
    }
}
