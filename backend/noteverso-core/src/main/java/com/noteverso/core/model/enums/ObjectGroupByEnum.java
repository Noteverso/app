package com.noteverso.core.model.enums;

import lombok.Getter;

@Getter
public enum ObjectGroupByEnum {
    NO_GROUP(0, "NO_GROUP"),
    ADDED_AT(1, "ADDED_AT"),
    UPDATED_AT(2, "UPDATED_AT"),
    NOTE_LABEL(3, "NOTE_LABEL");

    private final Integer value;
    private final String name;

    ObjectGroupByEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
