package com.noteverso.core.enums;


import lombok.Getter;

@Getter
public enum ObjectOrderByEnum {
    ADDED_AT(0, "ADDED_AT"),
    COMMENT_COUNT(1, "COMMENT_COUNT"),
    LINKED_NOTE_COUNT(2, "LINKED_NOTE_COUNT");

    private final Integer value;
    private final String name;

    ObjectOrderByEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
