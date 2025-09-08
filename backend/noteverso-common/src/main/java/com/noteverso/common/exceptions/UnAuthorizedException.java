package com.noteverso.common.exceptions;

import com.noteverso.common.api.ApiCode;

import java.io.Serial;

public class UnAuthorizedException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UnAuthorizedException() {
        super(ApiCode.UNAUTHORIZED.getCode(), ApiCode.UNAUTHORIZED.getMessage());
    }

    public UnAuthorizedException(String message, Throwable cause) {
        super(ApiCode.UNAUTHORIZED.getCode(), message, cause);
    }

    public UnAuthorizedException(String message) {
        super(ApiCode.UNAUTHORIZED.getCode(), message);
    }

    public UnAuthorizedException(Throwable cause) {
        super(ApiCode.UNAUTHORIZED.getCode(), cause);
    }
}
