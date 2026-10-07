package com.prueba.procesador.exception;

/** El archivo completo no se puede procesar (no existe, vacio o cabecera incorrecta). */
public class ArchivoInvalidoException extends RuntimeException {

    public ArchivoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
