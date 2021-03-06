package com.clup.aws.sqsdemo.config;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.profile.internal.securitytoken.RoleInfo;
import com.amazonaws.auth.profile.internal.securitytoken.STSProfileCredentialsServiceProvider;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.clup.aws.sqsdemo.SQSMessageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
public class AwsConfiguration {

    private String kmsKeyArn;

    private static final Logger logger = LoggerFactory.getLogger(AwsConfiguration.class);



    @Bean(name = "awsCredentialProvider")
    @Primary
    public AWSCredentialsProvider credentialProvider() {

       /* Credentials sessionCredentials = null;
        BasicSessionCredentials awsCredentials = null;
        try {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(envConfig.getStsUserAccessKey(), envConfig.getStsUserSecretKey());
            AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .withRegion(awsRegion)
                    .build();

            AssumeRoleRequest assumeRole = new AssumeRoleRequest()
                    .withRoleSessionName("sts_test_session")
                    .withRoleArn(roleARN)
                    .withDurationSeconds(Integer.parseInt(stsExpiry.trim()));

            AssumeRoleResult roleResponse = stsClient.assumeRole(assumeRole);
            sessionCredentials = roleResponse.getCredentials();

            awsCredentials = new BasicSessionCredentials(
                    sessionCredentials.getAccessKeyId(),
                    sessionCredentials.getSecretAccessKey(),
                    sessionCredentials.getSessionToken());
            //  log.info("the access key {} secret key {} token value {}", sessionCredentials.getAccessKeyId(),sessionCredentials.getSecretAccessKey(),sessionCredentials.getSessionToken());
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        return awsCredentials;*/

        List<AWSCredentialsProvider> providers = new ArrayList<>();
        System.setProperty(SDKGlobalConfiguration.ACCESS_KEY_SYSTEM_PROPERTY, "<IAM User access key goes here>");
        System.setProperty(SDKGlobalConfiguration.SECRET_KEY_SYSTEM_PROPERTY, "<IAM user secret key goes here>");
        providers.add(new EnvironmentVariableCredentialsProvider());
        STSProfileCredentialsServiceProvider sTSProfileCredentialsServiceProvider =  new STSProfileCredentialsServiceProvider(
                new RoleInfo().withRoleArn("<Role ARN Goes here>")
                        .withRoleSessionName(UUID.randomUUID().toString()));
        providers.add(sTSProfileCredentialsServiceProvider);

        logger.info("the received STS credentials are AccessKeyID: {} secretKey: {} ",sTSProfileCredentialsServiceProvider.getCredentials().getAWSAccessKeyId(),sTSProfileCredentialsServiceProvider.getCredentials().getAWSSecretKey());

        return new AWSCredentialsProviderChain(providers.toArray(new AWSCredentialsProvider[0]));
    }


    @Bean
    @Primary
    public AmazonSQSAsync sqsClient() {
        // AmazonSQSAsync sqs = AmazonSQSAsyncClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentialProvider()))
        AmazonSQSAsync sqs = AmazonSQSAsyncClientBuilder.standard().withCredentials(credentialProvider()).withRegion(Regions.AP_SOUTHEAST_2)
                .build();
        //System.out.println("initialized the bean");

        return sqs;
    }

    @Bean(name = "messagingTemplate")
    public QueueMessagingTemplate queueMessagingTemplate() {
        QueueMessagingTemplate template = new QueueMessagingTemplate(sqsClient());
        return template;
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory() {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(sqsClient());
        factory.setAutoStartup(true);
        factory.setMaxNumberOfMessages(5);
        factory.setVisibilityTimeout(3000);
        return factory;
    }

    @Bean(name = "kmsMasterKeyProvider")
    @Primary
    public KmsMasterKeyProvider getKmsMasterKeyProvider() {
        KmsMasterKeyProvider prov = KmsMasterKeyProvider.builder()
                .withCredentials(new BasicAWSCredentials("<IAM User access key goes here>", "<IAM User secret key goes here>"))
                .withDefaultRegion("ap-southeast-2")
                .withKeysForEncryption("<KMS key ARN>").build();
        return prov;
    }

    public String getKmsKeyArn() {
        return "<KMS ARN goes here>";
    }

    public void setKmsKeyArn(String kmsKeyArn) {
        this.kmsKeyArn = kmsKeyArn;
    }


}
