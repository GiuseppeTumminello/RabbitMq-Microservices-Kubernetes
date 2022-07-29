package com.acoustic.repository;

import com.acoustic.entity.SicknessZus;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SicknessZusDaoImplementation implements SicknessZusDao {

    public final DynamoDBMapper dynamoDBMapper;

    @Override
    public SicknessZus save(SicknessZus sicknessZus) {
        this.dynamoDBMapper.save(sicknessZus);
        return sicknessZus;
    }
}
