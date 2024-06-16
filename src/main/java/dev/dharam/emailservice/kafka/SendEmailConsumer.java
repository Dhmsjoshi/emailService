package dev.dharam.emailservice.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dharam.emailservice.kafka.dtos.SendEmailMessageDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;



@Service
public class SendEmailConsumer {

    private ObjectMapper objectMapper;
    private EmailUtil emailUtil;

    public SendEmailConsumer(ObjectMapper objectMapper, EmailUtil emailUtil) {
        this.objectMapper = objectMapper;
        this.emailUtil = emailUtil;
    }

    @KafkaListener(topics = "sendEmail", groupId = "emailService")
    public void handleSendEmailMessage(String message){

        //code to send an Email to user
        SendEmailMessageDto sendEmailMessageDto = null;

        try {
            sendEmailMessageDto  = objectMapper.readValue(message, SendEmailMessageDto.class);
        } catch (JsonProcessingException e) {
            System.out.println("Something went wrong!");
        }

        //send an Email
        //SMTP -> Simple Mail Trasfer Protocol

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS


        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("codeDharam@gmail.com", "puxazzjjkzpefrhi");
            }
        };

        Session session = Session.getInstance(props, auth);

        //finally send email

        emailUtil.sendEmail(
                session,
                sendEmailMessageDto.getTo(),
                sendEmailMessageDto.getSubject(),
                sendEmailMessageDto.getBody()
        );



    }
}
