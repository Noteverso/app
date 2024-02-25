package com.noteverso.core.constant;

public class CacheConstants {
    /**
     * 邮箱注册验证码
     */
    public static final String CACHE_VERIFICATION_CAPTCHA_CODE = "captcha:verification:%s:code";

    /**
     * 验证码创建时间
     */
    public static final String CACHE_VERIFICATION_CAPTCHA_CODE_CREATE_TIME = "captcha:verification:%s:create_time";

    /**
     * 同一个邮箱注册验证码发送次数
     */
    public static final String CACHE_VERIFICATION_CAPTCHA_CODE_SEND_TIMES = "captcha:verification:%s:send_times";

    /**
     * 邮箱验证码每天最多发送次数
     */
    public static final String CACHE_CAPTCHA_CODE_SEND_TIMES_TOTAL = "captcha:send_times_total";
}
