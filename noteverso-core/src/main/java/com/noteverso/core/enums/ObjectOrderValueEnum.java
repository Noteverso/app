package com.noteverso.core.enums;

import lombok.Getter;

@Getter
public enum ObjectOrderValueEnum {
    ASC(0, "ASC"),
    DESC(1, "DESC");

    private final Integer value;
    private final String name;

    ObjectOrderValueEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
