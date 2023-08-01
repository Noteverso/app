package com.noteverso.common.exceptions;

import java.io.Serial;

/**
 * Duplicate record exception
 */
public class DuplicateRecordException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateRecordException() {
    }

    public DuplicateRecordException(String message) {
        super(message);
    }

    public DuplicateRecordException(Throwable cause) {
        super(cause);
    }

    public DuplicateRecordException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateRecordException(int status, String message) {
        super(status, message);
    }

    public DuplicateRecordException(int status, Throwable cause) {
        super(status, cause);
    }

    public DuplicateRecordException(int status, String message, Throwable cause) {
        super(status, message, cause);
    }
}
