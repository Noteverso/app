package com.noteverso.core.service.impl;

import com.noteverso.core.service.EmailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private static final String SUPPORT_ADDRESS = "support@noteverso.com";

    private final JavaMailSender javaMailSender;
    private final SimpleMailMessage simpleMailMessage;

    @Override
    public void sendSimpleMessage(String to, String subject, String body) {
        try {
           SimpleMailMessage message = new SimpleMailMessage();
           message.setFrom(SUPPORT_ADDRESS);
           message.setTo(to);
           message.setSubject(subject);
           message.setText(body);

           javaMailSender.send(message);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void sendHtmlMessage(String to, String subject, String body) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            messageHelper.setFrom(SUPPORT_ADDRESS);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText("some text <img src='cid:logo'>", true);
            messageHelper.addInline("logo", new ClassPathResource("logo.jpeg"));
            messageHelper.addAttachment("myDocument.pdf", new ClassPathResource("document.pdf"));
        };

        try {
            javaMailSender.send(messagePreparator);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void sendMailWithInlineResources(String to, String subject, String body, String fileToAttach) {
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setFrom(new InternetAddress(SUPPORT_ADDRESS));
            mimeMessage.setSubject(subject);

            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setText("<html><body><img src='cid:identifier1234'></body></html>", true);

            FileSystemResource res = new FileSystemResource(new File(fileToAttach));
            messageHelper.addInline("identifier1234", res);
        };

        try {
            javaMailSender.send(preparator);
        } catch(MailException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void sendMailWithAttachment(String to, String subject, String body, String fileToAttach) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            messageHelper.setFrom(SUPPORT_ADDRESS);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(body);

            FileSystemResource fileSystemResource = new FileSystemResource(new File(fileToAttach));
            messageHelper.addAttachment("logo.jpeg", fileSystemResource);
        };

        try {
            javaMailSender.send(preparator);
        } catch(MailException exception) {
            exception.printStackTrace();
        }
    }
}
