package com.prueba.procesador.dao;

import com.prueba.procesador.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexion {

    private Conexion() {
    }

    public static Connection abrir(AppConfig config) throws SQLException {
        return DriverManager.getConnection(config.dbUrl(), config.dbUser(), config.dbPassword());
    }
}
