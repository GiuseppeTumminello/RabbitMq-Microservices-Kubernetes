package com.acoustic.repository;

import com.acoustic.entity.Health;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HealthZusDaoImplementation implements HealthZusDao {

    public final DynamoDBMapper dynamoDBMapper;

    @Override
    public Health save(Health health) {
        this.dynamoDBMapper.save(health);
        return health;
    }
}
