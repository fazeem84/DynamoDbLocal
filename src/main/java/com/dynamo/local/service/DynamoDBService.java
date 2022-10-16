package com.dynamo.local.service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

@Slf4j
public class DynamoDBService {
    public static final long READ_CAPACITY_UNITS = 10L;
    public static final long CAPACITY_UNITS = 10L;
    private final DynamoDbClient  client;

    public DynamoDBService(DynamoDbClient client) {
        this.client = client;
    }
    public <T>  boolean createTable(final String tableName,final Class<T> type) {
        DynamoDbEnhancedClient enhancedClient=  DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
        DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean(type));
        ProvisionedThroughput  provisionedThroughput= ProvisionedThroughput.builder()
                .readCapacityUnits(READ_CAPACITY_UNITS)
                .writeCapacityUnits(CAPACITY_UNITS)
                .build();

        table.createTable(builder -> builder
                .provisionedThroughput(provisionedThroughput)
        );

        log.info("waiting for Table Createion");

        try (DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(client).build()) { // DynamoDbWaiter is Autocloseable
            ResponseOrException<DescribeTableResponse> response = waiter
                    .waitUntilTableExists(builder -> builder.tableName(tableName).build())
                    .matched();
            DescribeTableResponse tableDescription = response.response().orElseThrow(
                    () -> new RuntimeException(" table was not created."));
            log.info( "{} was created.",tableDescription.table().tableName() );
        }
        return true;
    }
    public <T> boolean insertOrUpdateItem(final String tableName,final T item,final Class<T> type){
        DynamoDbEnhancedClient enhancedClient =  DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
        DynamoDbTable<T> itemTable = enhancedClient.table(tableName,TableSchema.fromBean(type));
        itemTable.putItem(item);
        log.info("Inserted/Updated {} to table {}",item,tableName);
        return true;
    }

    public <T> T getItemById(final String tableName,final String id,final Class<T> type){
        DynamoDbEnhancedClient enhancedClient =  DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
        DynamoDbTable<T> itemTable = enhancedClient.table(tableName,TableSchema.fromBean(type));
        Key key = Key.builder().partitionValue(id).build();
        T result = itemTable.getItem(r -> r.key(key));
        log.info("Fetched Item With Id {} = {} ",id,result);
        return result;
    }

}
