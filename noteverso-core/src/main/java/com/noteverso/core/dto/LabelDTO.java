package com.noteverso.core.dto;

import lombok.Data;

@Data
public class LabelDTO {
    private String labelId;
    private String name;
    private String color;
    private String userId;
    private Integer isFavorite;
}
