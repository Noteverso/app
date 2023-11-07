package com.noteverso.attachment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AttachmentRequest {
    @Schema(description = "Name of Attachment")
    private String name;

    @Schema(description = "MIME type of Attachment", example = "image/jpeg, video/mp4")
    private String type;

    @Schema(description = "OSS url of Attachment")
    private String url;

    @Schema(description = "Resource type of Attachment", allowableValues = "image, file")
    private String resourceType;
}
