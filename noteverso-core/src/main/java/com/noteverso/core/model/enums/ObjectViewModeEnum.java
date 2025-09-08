package com.noteverso.core.model.enums;

import lombok.Getter;

@Getter
public enum ObjectViewModeEnum {
    /**
     * 0 - list （列表） 1 - board （看板）
     */
    LIST(0, "LIST"),
    BOARD(1, "BOARD");

    private final Integer value;
    private final String name;


    ObjectViewModeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
