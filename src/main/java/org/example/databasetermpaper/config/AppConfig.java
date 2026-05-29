package org.example.databasetermpaper.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record AppConfig(String appTitle, String dbUrl, String dbUser, String dbPassword) {
    public static AppConfig load() {
        Properties properties = new Properties();
        try (InputStream input = AppConfig.class.getResourceAsStream("/application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read application.properties", exception);
        }

        return new AppConfig(
                properties.getProperty("app.title", "AviaCashDesk"),
                value("DB_URL", properties.getProperty("db.url")),
                value("DB_USER", properties.getProperty("db.user")),
                value("DB_PASSWORD", properties.getProperty("db.password", ""))
        );
    }

    private static String value(String environmentName, String defaultValue) {
        String environmentValue = System.getenv(environmentName);
        return environmentValue == null || environmentValue.isBlank() ? defaultValue : environmentValue;
    }
}
