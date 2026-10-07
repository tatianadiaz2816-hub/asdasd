package com.prueba.procesador.service;

import com.prueba.procesador.model.Registro;
import com.prueba.procesador.model.ResultadoValidacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Valida los campos de una linea con el orden:
 * correlation_id, cliente, documento, email, monto, fecha.
 */
public class ValidadorService {

    public static final String CABECERA = "correlation_id,cliente,documento,email,monto,fecha";
    public static final int NUM_COLUMNAS = 6;

    private static final Pattern CORRELATION = Pattern.compile("^[A-Za-z0-9-]{1,50}$");
    private static final Pattern DOCUMENTO = Pattern.compile("^\\d{5,15}$");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public ResultadoValidacion validar(String[] campos) {
        List<String> errores = new ArrayList<>();

        if (campos.length != NUM_COLUMNAS) {
            errores.add("Se esperaban " + NUM_COLUMNAS + " columnas y llegaron " + campos.length);
            return new ResultadoValidacion(null, errores);
        }

        String correlationId = campos[0].trim();
        String cliente = campos[1].trim();
        String documento = campos[2].trim();
        String email = campos[3].trim();
        String montoTxt = campos[4].trim();
        String fechaTxt = campos[5].trim();

        if (correlationId.isEmpty()) {
            errores.add("correlation_id es obligatorio");
        } else if (!CORRELATION.matcher(correlationId).matches()) {
            errores.add("correlation_id invalido (solo letras, numeros y guion, maximo 50)");
        }

        if (cliente.isEmpty()) {
            errores.add("cliente es obligatorio");
        } else if (cliente.length() > 100) {
            errores.add("cliente supera 100 caracteres");
        }

        if (documento.isEmpty()) {
            errores.add("documento es obligatorio");
        } else if (!DOCUMENTO.matcher(documento).matches()) {
            errores.add("documento debe tener entre 5 y 15 digitos");
        }

        if (!email.isEmpty() && (email.length() > 150 || !EMAIL.matcher(email).matches())) {
            errores.add("email con formato invalido");
        }

        BigDecimal monto = null;
        if (montoTxt.isEmpty()) {
            errores.add("monto es obligatorio");
        } else {
            try {
                monto = new BigDecimal(montoTxt);
                if (monto.signum() <= 0) {
                    errores.add("monto debe ser mayor a cero");
                } else if (monto.scale() > 2 || monto.precision() - monto.scale() > 13) {
                    errores.add("monto admite maximo 13 enteros y 2 decimales");
                }
            } catch (NumberFormatException e) {
                errores.add("monto no es un numero valido");
            }
        }

        LocalDate fecha = null;
        if (fechaTxt.isEmpty()) {
            errores.add("fecha es obligatoria");
        } else {
            try {
                fecha = LocalDate.parse(fechaTxt);
                if (fecha.isAfter(LocalDate.now())) {
                    errores.add("fecha no puede ser futura");
                }
            } catch (DateTimeParseException e) {
                errores.add("fecha debe tener formato yyyy-MM-dd y ser una fecha real");
            }
        }

        if (!errores.isEmpty()) {
            return new ResultadoValidacion(null, errores);
        }
        return new ResultadoValidacion(
                new Registro(correlationId, cliente, documento, email.isEmpty() ? null : email, monto, fecha),
                errores);
    }
}
