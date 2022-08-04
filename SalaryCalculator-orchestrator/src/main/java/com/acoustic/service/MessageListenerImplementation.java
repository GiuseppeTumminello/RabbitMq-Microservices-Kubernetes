package com.acoustic.service;


import com.acoustic.awssettings.AwsSettings;
import com.acoustic.configuartion.AwsSqsConfiguration;
import com.acoustic.entity.MicroservicesData;
import com.acoustic.repository.MicroservicesDataDao;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MessageListenerImplementation implements MessageListenerService {

   private final AwsSettings awsSettings;
   private final AwsSqsConfiguration awsSqsConfiguration;

   private final MicroservicesDataDao microservicesDataDao;
   private final ObjectMapper objectMapper;



   @Override
   public void run(){
      ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(awsSettings.getQueueUrl()).withMaxNumberOfMessages(10);
       while (true){
         var messageList = this.awsSqsConfiguration.awsSqsAsync().receiveMessage(receiveMessageRequest).getMessages();
         if (!messageList.isEmpty()) {
             for (Message message : messageList) {
                 var microservicesData = convertJsonToSicknessZusObject(message.getBody());
                 microservicesDataDao.save(microservicesData);
                 this.awsSqsConfiguration.awsSqsAsync().deleteMessage(this.awsSettings.getQueueUrl(), message.getReceiptHandle());
                 System.out.println("Data Saved successfully");
             }
         }
      }
   }


   @SneakyThrows
   public MicroservicesData convertJsonToSicknessZusObject(String snsMessage) {
      return this.objectMapper.readValue(snsMessage, MicroservicesData.class);

   }

}
