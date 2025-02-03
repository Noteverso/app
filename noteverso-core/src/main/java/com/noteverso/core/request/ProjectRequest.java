package com.noteverso.core.request;

import lombok.Data;

import java.util.Set;

@Data
public class ProjectRequest {
    private String name;

    private Set<String> projectIds;
}
