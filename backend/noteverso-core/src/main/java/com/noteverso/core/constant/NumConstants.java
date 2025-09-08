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

    /**
     * 验证码过期时间，单位秒
     */
    public static final int CAPTCHA_EXPIRE = 5 * 60;

    /**
     * 验证码发送间隔时间，单位毫秒
     */
    public static final int CAPTCHA_INTERVAL = 1 * 60 * 1000;

    /**
     * 同一个邮箱验证码最多发送次数
     */
    public static final int CAPTCHA_SEND_TIMES = 10;

    /**
     * 邮箱验证每天发送总次数
     */
    public static final int CAPTCHA_SEND_TIMES_TOTAL = 300;
}
