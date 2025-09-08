package com.noteverso.common.exceptions;

import com.noteverso.common.api.ApiCode;

import java.io.Serial;

public class NoPermissionException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NoPermissionException() {
        super(ApiCode.FORBIDDEN.getCode(), ApiCode.FORBIDDEN.getMessage());
    }

    public NoPermissionException(Throwable cause) {
        super(ApiCode.FORBIDDEN.getCode(), cause);
    }

    public NoPermissionException(String message, Throwable cause) {
        super(ApiCode.FORBIDDEN.getCode(), message, cause);
    }

    public NoPermissionException(String msg) {
        super(ApiCode.FORBIDDEN.getCode(), msg);
    }

}
