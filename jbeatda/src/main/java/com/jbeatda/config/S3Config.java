package com.jbeatda.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn("envConfig")
public class S3Config {

    @Value("${cloud.aws.credentials.access-key-id}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-access-key}")
    private String secretKey;

    @Value("${cloud.region.static}")
    private String region;

    @Bean
    public AmazonS3Client amazonS3Client() {


        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}