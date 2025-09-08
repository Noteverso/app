package com.noteverso.core.model.dto;

import lombok.Data;

@Data
public class LabelDTO {
    private String labelId;
    private String name;
    private String color;
    private String userId;
    private Integer isFavorite;
}
