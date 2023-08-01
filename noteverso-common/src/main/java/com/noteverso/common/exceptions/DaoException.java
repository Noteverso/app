package com.noteverso.common.exceptions;

import java.io.Serial;

/**
 * Dao exception
 */
public class DaoException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DaoException() {
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoException(int status, String message) {
        super(status, message);
    }

    public DaoException(int status, Throwable cause) {
        super(status, cause);
    }

    public DaoException(int status, String message, Throwable cause) {
        super(status, message, cause);
    }
}
