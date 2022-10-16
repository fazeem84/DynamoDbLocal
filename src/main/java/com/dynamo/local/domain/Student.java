package com.dynamo.local.domain;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
public class Student {
    private String studentId;
    private String studentName;
    private String department;
    @DynamoDbPartitionKey
    @DynamoDbAttribute("studentId")
    public String getStudentId() {
        return studentId;
    }
}
