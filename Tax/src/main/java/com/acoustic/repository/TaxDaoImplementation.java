package com.acoustic.repository;

import com.acoustic.entity.Tax;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TaxDaoImplementation implements TaxDao {

    public final DynamoDBMapper dynamoDBMapper;

    @Override
    public Tax save(Tax sicknessZus) {
        this.dynamoDBMapper.save(sicknessZus);
        return sicknessZus;
    }
}
