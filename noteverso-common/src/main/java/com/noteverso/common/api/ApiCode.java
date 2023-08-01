package com.noteverso.common.api;

/**
 * <p>
 * API响应码
 * 1000~1100 登陆模块
 * 4000~4100 数据库模块
 * 5000~5100 系统模块
 * 5100~5200 业务模块
 * </p>
 */

public enum ApiCode {
    /**
     * 操作成功
     */
    SUCCESS(0, "操作成功"),

    /**
     * 非法访问，未登录
     */
    UNAUTHORIZED(401, "非法访问，未登录"),

    /**
     * 非法访问，无权限
     */
    FORBIDDEN(403, "非法访问，无权限"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "您请求的资源不存在"),

    /**
     * 操作失败
     */
    FAIL(500, "操作失败"),

    /**
     * 登陆失败
     */
    LOGIN_FAIL(1000, "登陆失败"),

    /**
     * 登陆授权异常
     */
    LOGIN_AUTH_FAIL(1001, "登陆授权异常"),

    /**
     * 验证码校验异常
     */
    VERIFY_CODE_FAIL(1002, "验证码校验异常"),

    /**
     * Token 解析异常
     */
    TOKEN_PARSE_FAIL(1003, "Token 解析异常"),

    /**
     * 数据库处理异常
     */
    DAO_EXCEPTION(4000, "数据库处理异常"),

    /**
     * 数据记录重复
     */
    DUPLICATE_RECORD(4001, "数据记录重复"),

    /**
     * 系统异常
     */
    SYSTEM_EXCEPTION(5000, "系统异常"),

    /**
     * 请求参数校验异常
     */
    PARAMETER_EXCEPTION(5001, "请求参数校验异常"),

    /**
     * 请求参数解析异常
     */
    PARAMETER_PARSE_EXCEPTION(5002, "请求参数解析异常"),

    /**
     * HTTP 内容类型异常
     */
    HTTP_MEDIA_TYPE_EXCEPTION(5003, "HTTP 内容类型异常"),

    /**
     * HTTP 请求方法异常
     */
    HTTP_REQUEST_METHOD_EXCEPTION(5004, "HTTP 请求方法异常"),

    /**
     * 业务异常
     */
    BUSINESS_EXCEPTION(5101, "业务异常"),


    ;

    private final int code;

    private final String message;

    ApiCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ApiCode getApiCode(int code) {
        ApiCode[] apiCodes = ApiCode.values();
        for (ApiCode apiCode : apiCodes) {
            if (apiCode.getCode() == code) {
                return apiCode;
            }
        }
        return SUCCESS;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
