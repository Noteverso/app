package com.noteverso.core.model.dto;

import lombok.Data;

@Data
public class LabelItem {
    private String labelId;

    private String color;

    private String name;

    private Long noteCount;
}
