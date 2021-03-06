package com.clup.aws.sqsdemo.services;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class SQSMessageReceiver {

    private static final Logger logger = LoggerFactory.getLogger(SQSMessageReceiver.class);


    @SqsListener(value = "<SQS url goes here>", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
    public void receiveMessage(@Payload String message, final Acknowledgment acknowledgment,
                               @Headers Map<String, String> headerMap) {

        try {


            logger.info("the received message from SQS is {} ",message);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            acknowledgment.acknowledge();
           // logger.info("Exception occured in retry process and the exception is:: " + e+ ":-for data::"+message);
        }

    }
}
