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

    public static ObjectOrderValueEnum fromValue(Integer value) {
        for (ObjectOrderValueEnum type : ObjectOrderValueEnum.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
