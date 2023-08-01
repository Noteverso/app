package com.noteverso.common.exceptions;

import java.io.Serial;

/**
 * 请求验证不通过异常
 */
public class RequestVerifyException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    public RequestVerifyException() {
    }

    public RequestVerifyException(String message) {
        super(message);
    }

    public RequestVerifyException(Throwable cause) {
        super(cause);
    }

    public RequestVerifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestVerifyException(int status, String message) {
        super(status, message);
    }

    public RequestVerifyException(int status, Throwable cause) {
        super(status, cause);
    }

    public RequestVerifyException(int status, String message, Throwable cause) {
        super(status, message, cause);
    }

}
