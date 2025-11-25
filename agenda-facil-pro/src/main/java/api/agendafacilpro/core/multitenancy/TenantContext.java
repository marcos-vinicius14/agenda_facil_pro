package api.agendafacilpro.core.multitenancy;

import java.util.UUID;

public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenant(UUID tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static UUID getTenant() {
        return CURRENT_TENANT.get();
    }

    public static UUID getTenantOrThrow() {
        UUID id = getTenant();
        if (id == null) {
            throw new IllegalStateException("Tenant context is not initialized. Is this running outside a web request?");
        }
        return id;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}