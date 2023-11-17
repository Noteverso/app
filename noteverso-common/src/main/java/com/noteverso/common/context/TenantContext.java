package com.noteverso.common.context;

public final class TenantContext {
    private static Long tenantId;
    public TenantContext() {}

    public static Long getTenantId() {
        return tenantId;
    }

    public static void setTenantId(Long userId) {
        tenantId = userId;
    }
}
