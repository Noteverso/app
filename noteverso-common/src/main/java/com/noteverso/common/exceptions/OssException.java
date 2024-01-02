package com.noteverso.common.exceptions;

import java.io.Serial;

public class OssException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    public OssException() {}

    public OssException(String message) {
        super(message);
    }

    public OssException(String message, Throwable cause) {
        super(message, cause);
    }

    public OssException(int status, String message) {
        super(status, message);
    }

    public OssException(int status, Throwable cause) {
        super(status, cause);
    }

    public OssException(int status, String message, Throwable cause) {
        super(status, message, cause);
    }
}
