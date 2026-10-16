package freehold.production.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${aws.endpoint:}")
    private String awsEndpoint;

    @Value("${aws.region}")
    private String awsRegion;

    private static final String credentials = "test";

    @Bean
    public SqsClient sqsClient(){
        SqsClientBuilder builder = SqsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(credentials, credentials)));


        // when AWS_ENDPOINT is set (Docker), requests go to LocalStack.
        // When it’s blank (no Docker), the SDK uses its default AWS endpoint.
        if(awsEndpoint != null && !awsEndpoint.isBlank()) {
            builder.endpointOverride(URI.create(awsEndpoint));
        }
        return builder.build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient(){
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(credentials, credentials)));

        if(awsEndpoint != null && !awsEndpoint.isBlank()) {
            builder.endpointOverride(URI.create(awsEndpoint));
        }
        return builder.build();
    }



}
