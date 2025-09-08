package com.noteverso.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
public class ApiResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public ApiResult() {
    }

    public static ApiResult<Boolean> success() {
        return success(null);
    }

    public static <T> ApiResult<T> success(T data) {
        return result(ApiCode.SUCCESS, data);
    }

    public static <T> ApiResult<T> success(T data, String message) {
        return result(ApiCode.SUCCESS, message, data);
    }

    public static <T> ApiResult<T> result(ApiCode apiCode) {
        return result(apiCode, null);
    }

    public static <T> ApiResult<T> result(ApiCode apiCode, T Data) {
        return result(apiCode, null, Data);
    }

    public static <T> ApiResult<T> result(ApiCode apiCode, String message, T Data) {
        String apiMessage = apiCode.getMessage();
        if (StringUtils.isNotBlank(message)) {
            apiMessage = message;
        }
        return ApiResult.<T>builder()
                .code(apiCode.getCode())
                .message(apiMessage)
                .data(Data)
                .build();
    }

    public static ApiResult<Boolean> fail() {
        return fail(ApiCode.FAIL);
    }

    public static ApiResult<Boolean> fail(ApiCode apicode) {
        return fail(apicode, null);
    }

    public static <T> ApiResult<T> fail(String message) {
        return result(ApiCode.FAIL, message, null);
    }

    public static <T> ApiResult<T> fail(ApiCode apiCode, T data) {
        if (ApiCode.SUCCESS == apiCode) {
            throw new RuntimeException("apiCode can not be " + ApiCode.SUCCESS.getCode());
        }
        return result(apiCode, data);
    }
}
