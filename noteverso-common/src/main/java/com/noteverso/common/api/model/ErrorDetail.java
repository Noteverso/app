package com.noteverso.common.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Error Detail
 */

@Getter
@Setter
public class ErrorDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = 265990983744831L;

    private int code;

    private String message;

    private String type;

}
