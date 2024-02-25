package com.noteverso.core.request;

import lombok.Data;

@Data
public class MailRequest {
    private String subject;

    private String body;

    private String to;

    private String filePath;
}
