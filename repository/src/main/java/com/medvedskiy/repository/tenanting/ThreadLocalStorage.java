package com.medvedskiy.repository.tenanting;

public class ThreadLocalStorage {

    private static final ThreadLocal<String> tenant = new ThreadLocal<>();

    private static int databaseCount = 0;

    public static String getTenantName() {
        return tenant.get();
    }

    public static void setTenantName(String tenantName) {
        tenant.set(tenantName);
    }

    public static int getDatabaseCount() {
        return databaseCount;
    }

    public static void setDatabaseCount(int databaseCount) {
        if (ThreadLocalStorage.databaseCount == 0) {
            ThreadLocalStorage.databaseCount = databaseCount;
        }
    }
}

