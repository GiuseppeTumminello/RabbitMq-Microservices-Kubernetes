package com.acoustic.repository;

import com.acoustic.entity.MicroservicesData;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MicroservicesDataDaoImplementation implements MicroservicesDataDao {

    public final DynamoDBMapper dynamoDBMapper;

    public static final String UUID_COLUMN_NAME = "uuid";



    @Override
    public MicroservicesData save(MicroservicesData Microservices) {
        this.dynamoDBMapper.save(Microservices);
        return Microservices;

    }


    @Override
    public List<MicroservicesData> findByUuid(String uuid) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.addFilterCondition(UUID_COLUMN_NAME, new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(uuid)));
        return dynamoDBMapper.scan(MicroservicesData.class, scanExpression);

    }

}
