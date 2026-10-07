package com.prueba.procesador.model;

/** Linea rechazada con su motivo. {@code correlationId} puede ser null si no se pudo leer. */
public record Rechazo(
        int numeroLinea,
        String correlationId,
        String contenido,
        MotivoRechazo motivo,
        String detalle) {
}
