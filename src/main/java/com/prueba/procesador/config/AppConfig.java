package com.prueba.procesador.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/** Lee application.properties; las variables de entorno DB_URL, DB_USER y DB_PASSWORD tienen prioridad. */
public final class AppConfig {

    private final Properties props = new Properties();

    public AppConfig() {
        try (InputStream in = AppConfig.class.getResourceAsStream("/application.properties")) {
            if (in == null) {
                throw new IllegalStateException("No se encontro application.properties en el classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer application.properties", e);
        }
    }

    public String dbUrl() {
        return valor("db.url", "DB_URL");
    }

    public String dbUser() {
        return valor("db.user", "DB_USER");
    }

    public String dbPassword() {
        return valor("db.password", "DB_PASSWORD");
    }

    private String valor(String clave, String variableEntorno) {
        String env = System.getenv(variableEntorno);
        return (env != null && !env.isBlank()) ? env : props.getProperty(clave, "");
    }
}
