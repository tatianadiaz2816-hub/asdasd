package com.prueba.procesador.service;

import com.prueba.procesador.dao.ProcesamientoDao;
import com.prueba.procesador.dao.RechazoDao;
import com.prueba.procesador.dao.RegistroDao;
import com.prueba.procesador.exception.ArchivoInvalidoException;
import com.prueba.procesador.model.MotivoRechazo;
import com.prueba.procesador.model.Rechazo;
import com.prueba.procesador.model.ResultadoValidacion;
import com.prueba.procesador.model.ResumenProcesamiento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Orquesta el flujo: leer archivo, validar, controlar duplicados, guardar y dejar trazabilidad. */
public class ProcesadorService {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorService.class);

    private final ValidadorService validador;
    private final RegistroDao registroDao;
    private final RechazoDao rechazoDao;
    private final ProcesamientoDao procesamientoDao;

    public ProcesadorService(ValidadorService validador, RegistroDao registroDao,
                             RechazoDao rechazoDao, ProcesamientoDao procesamientoDao) {
        this.validador = validador;
        this.registroDao = registroDao;
        this.rechazoDao = rechazoDao;
        this.procesamientoDao = procesamientoDao;
    }

    public ResumenProcesamiento procesar(Path archivo) throws SQLException {
        ResumenProcesamiento resumen = new ResumenProcesamiento();
        long procesamientoId = procesamientoDao.iniciar(archivo.getFileName().toString());
        log.info("Procesamiento {} iniciado para el archivo {}", procesamientoId, archivo);

        try {
            List<String> lineas = leerLineas(archivo);
            validarCabecera(lineas);
            procesarLineas(lineas, procesamientoId, resumen);

            boolean conRechazos = resumen.getInvalidos() + resumen.getDuplicados() > 0;
            procesamientoDao.finalizar(procesamientoId,
                    conRechazos ? "COMPLETADO_CON_RECHAZOS" : "COMPLETADO", resumen, null);
            log.info("Procesamiento {} finalizado: {}", procesamientoId, resumen);
            return resumen;
        } catch (ArchivoInvalidoException | SQLException e) {
            registrarError(procesamientoId, resumen, e);
            throw e;
        } catch (IOException e) {
            registrarError(procesamientoId, resumen, e);
            throw new ArchivoInvalidoException("No se pudo leer el archivo: " + e.getMessage());
        }
    }

    private void registrarError(long procesamientoId, ResumenProcesamiento resumen, Exception e) throws SQLException {
        log.error("Procesamiento {} fallido: {}", procesamientoId, e.getMessage());
        procesamientoDao.finalizar(procesamientoId, "ERROR", resumen, e.getMessage());
    }

    private List<String> leerLineas(Path archivo) throws IOException {
        if (!Files.isRegularFile(archivo)) {
            throw new ArchivoInvalidoException("El archivo no existe: " + archivo);
        }
        return Files.readAllLines(archivo, StandardCharsets.UTF_8);
    }

    private void validarCabecera(List<String> lineas) {
        if (lineas.isEmpty() || lineas.get(0).isBlank()) {
            throw new ArchivoInvalidoException("El archivo esta vacio");
        }
        String cabecera = lineas.get(0).replace("﻿", "").trim();
        if (!cabecera.equalsIgnoreCase(ValidadorService.CABECERA)) {
            throw new ArchivoInvalidoException("Cabecera invalida. Se esperaba: " + ValidadorService.CABECERA);
        }
    }

    private void procesarLineas(List<String> lineas, long procesamientoId, ResumenProcesamiento resumen)
            throws SQLException {
        Set<String> vistosEnArchivo = new HashSet<>();

        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            if (linea.isBlank()) {
                continue;
            }
            int numeroLinea = i + 1;
            resumen.sumarLeido();

            String[] campos = linea.split(",", -1);
            String correlationId = campos[0].trim().isEmpty() ? null : campos[0].trim();

            ResultadoValidacion resultado = validador.validar(campos);
            if (!resultado.esValido()) {
                rechazar(procesamientoId, new Rechazo(numeroLinea, correlationId, linea, MotivoRechazo.INVALIDO,
                        String.join("; ", resultado.errores())));
                resumen.sumarInvalido();
                continue;
            }

            if (!vistosEnArchivo.add(correlationId)) {
                rechazar(procesamientoId, new Rechazo(numeroLinea, correlationId, linea, MotivoRechazo.DUPLICADO,
                        "correlation_id repetido dentro del mismo archivo"));
                resumen.sumarDuplicado();
            } else if (!registroDao.insertarSiNoExiste(resultado.registro(), procesamientoId)) {
                rechazar(procesamientoId, new Rechazo(numeroLinea, correlationId, linea, MotivoRechazo.DUPLICADO,
                        "correlation_id ya existe en la base de datos"));
                resumen.sumarDuplicado();
            } else {
                resumen.sumarValido();
            }
        }
    }

    private void rechazar(long procesamientoId, Rechazo rechazo) throws SQLException {
        log.warn("Linea {} rechazada ({}): {}", rechazo.numeroLinea(), rechazo.motivo(), rechazo.detalle());
        rechazoDao.insertar(procesamientoId, rechazo);
    }
}
