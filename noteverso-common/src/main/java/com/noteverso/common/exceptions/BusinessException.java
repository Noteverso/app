package com.noteverso.common.exceptions;

import java.io.Serial;

/**
 * Business exception
 * 业务层通用异常（一般在service中抛出，service中的异常继承该异常）
 */
public class BusinessException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(int status, String message) {
        super(status, message);
    }

    public BusinessException(int status, Throwable cause) {
        super(status, cause);
    }

    public BusinessException(int status, String message, Throwable cause) {
        super(status, message, cause);
    }
}
