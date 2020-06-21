package org.ccctc.common.docker.localstack;

import static cloud.localstack.TestUtils.TEST_CREDENTIALS;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class LocalstackEndpoints {

    public static final String REGION = "us-west-2";
    public static final String S3_ENDPOINT_PATH = "http://localhost:4572";
    public static final String SNS_ENDPOINT_PATH = "http://localhost:4575";
    public static final String SQS_ENDPOINT_PATH = "http://localhost:4576";

    public static AWSCredentialsProvider getCredentialsProvider() {
        return new AWSStaticCredentialsProvider(TEST_CREDENTIALS);
    }

    public static AmazonSNS snsClient() {
        AwsClientBuilder.EndpointConfiguration snsEndpointConfiguration = new AwsClientBuilder.EndpointConfiguration(SNS_ENDPOINT_PATH, REGION);
        AmazonSNS snsClient = AmazonSNSClientBuilder.standard()
                .withEndpointConfiguration(snsEndpointConfiguration)
                .withCredentials(getCredentialsProvider())
                .build();
        return snsClient;
    }

    public static AmazonSQS sqsClient() {
        AwsClientBuilder.EndpointConfiguration sqsEndpointConfiguration = new AwsClientBuilder.EndpointConfiguration(SQS_ENDPOINT_PATH, REGION);
        AmazonSQS sqsClient = AmazonSQSClientBuilder.standard()
                .withEndpointConfiguration(sqsEndpointConfiguration)
                .withCredentials(getCredentialsProvider())
                .build();
        return sqsClient;
    }
}
