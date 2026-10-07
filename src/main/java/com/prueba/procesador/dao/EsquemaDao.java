package com.prueba.procesador.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Crea las tablas si no existen, ejecutando schema.sql. */
public class EsquemaDao {

    private final Connection conexion;

    public EsquemaDao(Connection conexion) {
        this.conexion = conexion;
    }

    public void crearSiNoExiste() throws SQLException {
        String script = leerScript();
        try (Statement st = conexion.createStatement()) {
            for (String sentencia : script.split(";")) {
                if (!sentencia.isBlank()) {
                    st.execute(sentencia);
                }
            }
        }
    }

    private String leerScript() {
        try (InputStream in = EsquemaDao.class.getResourceAsStream("/schema.sql")) {
            if (in == null) {
                throw new IllegalStateException("No se encontro schema.sql en el classpath");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer schema.sql", e);
        }
    }
}
