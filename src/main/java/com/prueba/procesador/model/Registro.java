package com.prueba.procesador.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Registro ya validado, listo para almacenar. {@code email} puede ser null. */
public record Registro(
        String correlationId,
        String cliente,
        String documento,
        String email,
        BigDecimal monto,
        LocalDate fecha) {
}
