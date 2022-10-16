package com.dynamolocal.service;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.dynamo.local.domain.Student;
import com.dynamo.local.service.DynamoDBService;
import com.dynamolocal.util.AwsDynamoDbLocalTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

public class DynamoServiceTest {
    public static final String TABLE_NAME = "Student";
    private static  DynamoDBProxyServer server;
    private static final String port = "8000";
    private static final String uri = "http://localhost:"+port;
    DynamoDbClient client;

    @BeforeEach
    public void setUpTest() throws Exception {
        AwsDynamoDbLocalTestUtils.initSqLite();
        server = ServerRunner.createServerFromCommandLineArgs(
                new String[]{"-inMemory", "-port", port});
        server.start();
        client=DynamoDbClient.builder()
                .endpointOverride(URI.create(uri))
                .region(Region.AF_SOUTH_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("fakeMyKeyId","fakeSecretAccessKey")))
                .build();

    }
    @AfterEach
    public void tearDownTest() throws Exception {
        client.close();
        server.stop();

    }
    @Test
     void createTableTest(){
        Student student=new Student();
        student.setStudentId("TestId");
        student.setDepartment("ComputerScience");
        student.setStudentName("TestName");
        DynamoDBService dynamoDBService=new DynamoDBService(client);
        Assertions.assertTrue(dynamoDBService.createTable("Student",Student.class));

    }
    @Test
    void putItemTest(){
        Student student=new Student();
        student.setStudentId("TestId");
        student.setDepartment("ComputerScience");
        student.setStudentName("TestName");
        DynamoDBService dynamoDBService=new DynamoDBService(client);
        Assertions.assertTrue(dynamoDBService.createTable(TABLE_NAME,Student.class));
        Assertions.assertTrue(dynamoDBService.insertOrUpdateItem(TABLE_NAME,student,Student.class));

    }
    @Test
    void gettemTest(){
        Student student=new Student();
        student.setStudentId("TestId");
        student.setDepartment("ComputerScience");
        student.setStudentName("TestName");
        DynamoDBService dynamoDBService=new DynamoDBService(client);
        Assertions.assertTrue(dynamoDBService.createTable(TABLE_NAME,Student.class));
        Assertions.assertTrue(dynamoDBService.insertOrUpdateItem(TABLE_NAME,student,Student.class));
        Assertions.assertEquals(dynamoDBService.getItemById(TABLE_NAME,"TestId",Student.class),student);

    }

}
