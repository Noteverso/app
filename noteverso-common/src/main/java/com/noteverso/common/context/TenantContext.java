package com.noteverso.common.context;

public final class TenantContext {
    private static String tenantId;
    public TenantContext() {}

    public static String getTenantId() {
        return tenantId;
    }

    public static void setTenantId(String userId) {
        tenantId = userId;
    }
}
