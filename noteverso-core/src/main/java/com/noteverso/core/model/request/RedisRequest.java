package com.noteverso.core.model.request;

import lombok.Data;

@Data
public class RedisRequest {
    private String key;
    private Object value;
}
