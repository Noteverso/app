package com.noteverso.core.model.response;

import lombok.Data;

@Data
public class UploadFileGetResponse {
    private String signedPutUrl;

    private String signedGetUrl;

    private String attachmentUrl;
}
