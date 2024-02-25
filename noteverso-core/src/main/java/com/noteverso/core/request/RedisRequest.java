package com.noteverso.core.request;

import lombok.Data;

@Data
public class RedisRequest {
    private String key;
    private Object value;
}
