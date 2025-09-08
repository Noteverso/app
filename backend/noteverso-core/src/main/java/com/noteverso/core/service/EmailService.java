package com.noteverso.core.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String body);

//    void sendTemplateMessage(String to, String subject, String body);

    void sendHtmlMessage(String to, String subject, String body);

    void sendMailWithAttachment(String to, String subject, String body, String fileToAttach);

    void sendMailWithInlineResources(String to, String subject, String body, String fileToAttach);
}
