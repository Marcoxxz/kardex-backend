package com.universidad.kardex_backend.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class SchemaInterceptor implements CurrentTenantIdentifierResolver {

    private static final ThreadLocal<String> currentSchema = new ThreadLocal<>();

    public static void setCurrentSchema(String schema) {
        currentSchema.set(schema);
    }

    public static String getCurrentSchema() {
        return currentSchema.get();
    }

    public static void clear() {
        currentSchema.remove();
    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        String schema = currentSchema.get();
        return schema != null ? schema : "public";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}