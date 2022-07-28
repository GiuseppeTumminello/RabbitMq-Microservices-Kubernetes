package com.acoustic.repository;

import com.acoustic.entity.DisabilityZus;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DisabilityZusDaoImplementation implements DisabilityZusDao {

    public final DynamoDBMapper dynamoDBMapper;

    @Override
    public DisabilityZus save(DisabilityZus disabilityZus) {
        this.dynamoDBMapper.save(disabilityZus);
        return disabilityZus;
    }
}
