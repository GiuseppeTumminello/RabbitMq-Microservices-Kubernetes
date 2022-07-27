package com.acoustic.entity;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "monthly_gross")
@Builder
@Data
public class MonthlyGross {
    @DynamoDBAutoGeneratedKey
    @DynamoDBHashKey
    private String id;
    @DynamoDBAttribute
    private String description;
    @DynamoDBAttribute
    private BigDecimal amount;
    @DynamoDBAttribute
    private UUID uuid;


}
