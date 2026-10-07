package com.prueba.procesador.dao;

import com.prueba.procesador.model.Rechazo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RechazoDao {

    private final Connection conexion;

    public RechazoDao(Connection conexion) {
        this.conexion = conexion;
    }

    public void insertar(long procesamientoId, Rechazo r) throws SQLException {
        String sql = "INSERT INTO registros_rechazados (procesamiento_id, numero_linea, correlation_id, contenido, motivo, detalle) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, procesamientoId);
            ps.setInt(2, r.numeroLinea());
            ps.setString(3, r.correlationId());
            ps.setString(4, r.contenido());
            ps.setString(5, r.motivo().name());
            ps.setString(6, r.detalle());
            ps.executeUpdate();
        }
    }
}
