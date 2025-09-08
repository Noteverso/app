package com.noteverso.core.model.enums;

import lombok.Getter;

@Getter
public enum ObjectViewTypeEnum {
    PROJECT(0, "PROJECT"),
    UPCOMING(1, "UPCOMING"),
    TODAY(2, "TODAY"),
    // 定期回顾
    PAST(3, "PAST"),
    ATTACHMENT(4, "ATTACHMENT"),
    LABEL(5, "LABEL"),

    ;

    private final Integer value;
    private final String name;

    ObjectViewTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
