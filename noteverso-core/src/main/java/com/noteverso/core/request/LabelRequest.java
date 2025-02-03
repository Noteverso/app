package com.noteverso.core.request;

import lombok.Data;

import java.util.List;

@Data
public class LabelRequest {
    private String name;

    private String labelId;

    private List<String> labelIds;
}
