package com.noteverso.core.enums;


import lombok.Getter;

@Getter
public enum ObjectOrderByEnum {
    ADDED_AT(0, "added_at"),
    UPDATED_AT(1, "updated_at"),;

    private final Integer value;
    private final String name;

    ObjectOrderByEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ObjectOrderByEnum fromValue(Integer value) {
        for (ObjectOrderByEnum type : ObjectOrderByEnum.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
