package com.pt.service.impl;
import com.pt.entity.MailConfig;
import com.pt.entity.OrderItem;
import com.pt.service.SendEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
@Service
public class SendEmail implements SendEmailService {



    @Override
    public void sendEmailCreateOrder(String email, List<OrderItem> orderItems) {


        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", MailConfig.HOST_NAME);
        props.put("mail.smtp.socketFactory.port", MailConfig.SSL_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.port", MailConfig.SSL_PORT);

        // get Session
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MailConfig.APP_EMAIL, MailConfig.APP_PASSWORD);
            }
        });

        // compose message
        try {
            MimeMessage message = new MimeMessage(session);


            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Order Success");
            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<h1>The order has been placed successfully</h1>");
            emailContent.append("<p>Thank you for purchasing at TranhuuphucShop. Your order will be sent to you soon!!</p>");
            emailContent.append("<ul>");


            for (OrderItem order : orderItems) {
                emailContent.append("<li>");
                emailContent.append("Product's name: ").append(order.getName()).append("<br/>");
                emailContent.append("Quantity: ").append(order.getAmount()).append("<br/>");
                emailContent.append("</li>");
            }

            message.setContent(emailContent.toString(), "text/html");

            // send message
            Transport.send(message);

            System.out.println("Message sent successfully");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
