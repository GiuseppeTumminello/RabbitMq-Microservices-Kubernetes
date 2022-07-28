package com.acoustic.repository;

import com.acoustic.entity.PensionZus;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PensionZusDaoImplementation implements PensionZusDao {

    public final DynamoDBMapper dynamoDBMapper;

    @Override
    public PensionZus save(PensionZus monthlyNet) {
        this.dynamoDBMapper.save(monthlyNet);
        return monthlyNet;
    }
}
