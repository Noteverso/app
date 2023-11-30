package com.noteverso.core.enums;

import lombok.Getter;

@Getter
public enum ObjectGroupByEnum {
    ADDED_AT(0, "ADDED_AT"),
    NOTE_STATUS(1, "NOTE_STATUS"),
    NOTE_LABEL(2, "NOTE_LABEL");

    private final Integer value;
    private final String name;

    ObjectGroupByEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
