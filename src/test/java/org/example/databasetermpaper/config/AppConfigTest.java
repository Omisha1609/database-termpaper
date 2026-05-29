package org.example.databasetermpaper.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AppConfigTest {
    @Test
    void loadsDatabaseUrl() {
        AppConfig config = AppConfig.load();

        assertTrue(config.dbUrl().startsWith("jdbc:postgresql://"));
    }
}
