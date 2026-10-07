package com.prueba.procesador.dao;

import com.prueba.procesador.model.Registro;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistroDao {

    private final Connection conexion;

    public RegistroDao(Connection conexion) {
        this.conexion = conexion;
    }

    /**
     * Inserta el registro si su correlation_id no existe todavia.
     *
     * @return true si se inserto; false si ya existia (duplicado en base de datos)
     */
    public boolean insertarSiNoExiste(Registro r, long procesamientoId) throws SQLException {
        String sql = "INSERT INTO registros_validos (correlation_id, cliente, documento, email, monto, fecha, procesamiento_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (correlation_id) DO NOTHING";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, r.correlationId());
            ps.setString(2, r.cliente());
            ps.setString(3, r.documento());
            ps.setString(4, r.email());
            ps.setBigDecimal(5, r.monto());
            ps.setDate(6, Date.valueOf(r.fecha()));
            ps.setLong(7, procesamientoId);
            return ps.executeUpdate() == 1;
        }
    }
}
