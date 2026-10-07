package com.prueba.procesador.dao;

import com.prueba.procesador.model.ResumenProcesamiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Trazabilidad: una fila por cada archivo procesado. */
public class ProcesamientoDao {

    private final Connection conexion;

    public ProcesamientoDao(Connection conexion) {
        this.conexion = conexion;
    }

    public long iniciar(String nombreArchivo) throws SQLException {
        String sql = "INSERT INTO procesamiento_archivos (nombre_archivo, estado) VALUES (?, 'EN_PROCESO') RETURNING id";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreArchivo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public void finalizar(long id, String estado, ResumenProcesamiento resumen, String mensaje) throws SQLException {
        String sql = "UPDATE procesamiento_archivos SET fecha_fin = now(), estado = ?, total_leidos = ?, "
                + "total_validos = ?, total_invalidos = ?, total_duplicados = ?, mensaje = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, resumen.getLeidos());
            ps.setInt(3, resumen.getValidos());
            ps.setInt(4, resumen.getInvalidos());
            ps.setInt(5, resumen.getDuplicados());
            ps.setString(6, mensaje);
            ps.setLong(7, id);
            ps.executeUpdate();
        }
    }
}
