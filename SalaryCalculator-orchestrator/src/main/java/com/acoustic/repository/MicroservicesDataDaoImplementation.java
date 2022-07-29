package com.acoustic.repository;

import com.acoustic.entity.MicroservicesData2;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class MicroservicesDataDaoImplementation implements MicroservicesDataDao {

    public final DynamoDBMapper dynamoDBMapper;



    @Override
    public MicroservicesData2 save(MicroservicesData2 Microservices) {
        this.dynamoDBMapper.save(Microservices);
        return Microservices;
    }


    @Override
    public void getByName(String name) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.addFilterCondition("description", new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(name)));
        var data = new ArrayList<>(dynamoDBMapper.scan(MicroservicesData2.class, scanExpression));
        data.forEach(e -> System.out.println(e.getDescription() + " " + e.getAmount()));

    }
}
