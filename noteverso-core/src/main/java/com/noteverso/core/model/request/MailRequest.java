package com.noteverso.core.model.request;

import lombok.Data;

@Data
public class MailRequest {
    private String subject;

    private String body;

    private String to;

    private String filePath;
}
