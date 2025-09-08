package com.noteverso.core.model.request;

import lombok.Data;

import java.util.Set;

@Data
public class ProjectRequest {
    private String name;

    private Set<String> projectIds;
}
