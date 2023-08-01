package com.noteverso.common.exceptions;

import java.io.Serial;

public class BaseException extends StatefulException {
    @Serial
    private static final long serialVersionUID = 1L;

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(int status, String message) {
        super(status, message);
    }

    public BaseException(int status, Throwable cause) {
        super(status, cause);
    }

    public BaseException(int status, String message, Throwable cause) {
        super(status, message, cause);
    }

}
