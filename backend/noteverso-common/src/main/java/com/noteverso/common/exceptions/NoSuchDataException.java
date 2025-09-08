package com.noteverso.common.exceptions;

import java.io.Serial;

/**
 * No such data exception
 */

public class NoSuchDataException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NoSuchDataException() {
    }

    public NoSuchDataException(String message) {
        super(message);
    }

    public NoSuchDataException(Throwable cause) {
        super(cause);
    }

    public NoSuchDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchDataException(int status, String message) {
        super(status, message);
    }

    public NoSuchDataException(int status, Throwable cause) {
        super(status, cause);
    }

    public NoSuchDataException(int status, String message, Throwable cause) {
        super(status, message, cause);
    }
}
