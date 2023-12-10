package com.noteverso.core.constant;

public class NumConstants {
    /**
     * 5MB
     */
    public static final Long MAX_FILE_SIZE_NORMAL = 5 * 1024 * 1024L;
    /**
     * 100MB
     */
    public static final Long MAX_FILE_SIZE_PREMIUM = 100 * 1024 * 1024L;

    /**
     * 10GB
     */
    public static final Long FILE_SIZE_QUOTA_PREMIUM = 5 * 1024 * 1024 * 1024L;

    /**
     * 100MB
     */
    public static final Long FILE_SIZE_QUOTA_NORMAL = 100  * 1024 * 1024L;

    /**
     * 项目数量配额
     */
    public static final Long PROJECT_QUOTA_NORMAL = 5L;

    /**
     * 项目数量配额
     */
    public static final Long PROJECT_QUOTA_PREMIUM = 100L;

    /**
     * 笔记链接数配额
     */
    public static final Long LINKED_NOTE_QUOTA_NORMAL = 50L;

    /**
     * 笔记链接数配额
     */
    public static final Long LINKED_NOTE_QUOTA_PREMIUM = 10000L;
}
