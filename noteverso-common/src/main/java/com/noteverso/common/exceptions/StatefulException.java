package com.noteverso.common.exceptions;

import java.io.Serial;

/**
 * 带有状态码的异常
 */
public class StatefulException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 607338692016364795L;

    /**
     * 状态码
     */
    protected int status;

    public StatefulException() {
    }

    public StatefulException(String msg) {
        super(msg);
    }

    public StatefulException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public StatefulException(Throwable cause) {
        super(cause);
    }

    public StatefulException(int status, String msg) {
        super(msg);
        this.status = status;
    }

    public StatefulException(int status,  Throwable cause) {
        super(cause);
        this.status = status;
    }

    /**
     * 获取状态码
     * @return 状态码
     */
    public int getStatus() {
        return status;
    }

    public StatefulException(int status, String msg, Throwable cause) {
        super(msg, cause);
        this.status = status;
    }

}
