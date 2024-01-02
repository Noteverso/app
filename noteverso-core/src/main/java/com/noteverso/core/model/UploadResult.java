package com.noteverso.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadResult {
    /**
     * 文件路径
     */
    private String url;

    /**
     * 文件名
     */
    private String fileName;
}
