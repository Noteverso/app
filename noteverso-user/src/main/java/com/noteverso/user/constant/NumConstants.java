package com.noteverso.user.constant;

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
    public static final Integer PROJECT_QUOTA_NORMAL = 20;

    /**
     * 项目数量配额
     */
    public static final Integer PROJECT_QUOTA_PREMIUM = 100;

    /**
     * 笔记链接数配额
     */
    public static final Integer LINKED_NOTE_QUOTA_NORMAL = 50;

    /**
     * 笔记链接数配额
     */
    public static final Integer LINKED_NOTE_QUOTA_PREMIUM = 10000;
}
