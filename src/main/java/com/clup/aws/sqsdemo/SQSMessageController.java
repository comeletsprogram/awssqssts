package com.clup.aws.sqsdemo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SQSMessageController {
    private static final Logger logger = LoggerFactory.getLogger(SQSMessageController.class);

    @Autowired
    QueueMessagingTemplate queueMessagingTemplate;

    @PostMapping(path = "/sendmessage")
    public ResponseEntity<String> sendMessage(@RequestBody String message){

        logger.info("meesage received at controller {}",message);
        queueMessagingTemplate.convertAndSend("johnweslysdv", message);
        return new ResponseEntity<>( "meesage has been sent ",HttpStatus.OK);
    }

}
