package com.noteverso.common.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Error response
 */

@Getter
@Setter
public class ErrorResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 7094663127737991899L;

    private ErrorDetail error;
}
