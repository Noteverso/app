package com.noteverso.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AttachmentDetail {
    @Schema(description = "Name of Attachment", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "MIME type of Attachment", example = "image/jpeg, video/mp4", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @Schema(description = "OSS url of Attachment", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    @Schema(description = "Resource type of Attachment", allowableValues = "0 - image, 1 - file", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer resourceType;

    @Schema(description = "Resource size of Attachment, unit MB", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long size;
}
