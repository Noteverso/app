package com.noteverso.core.response;

import lombok.Data;

@Data
public class UploadFileGetResponse {
    private String signedPutUrl;

    private String signedGetUrl;

    private String attachmentUrl;
}
