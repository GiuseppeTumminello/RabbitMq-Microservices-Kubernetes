package com.acoustic.repository;

import com.acoustic.entity.TotalZus;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TotalZusDaoImplementation implements TotalZusDao {

    public final DynamoDBMapper dynamoDBMapper;

    @Override
    public TotalZus save(TotalZus totalZus) {
        this.dynamoDBMapper.save(totalZus);
        return totalZus;
    }
}
