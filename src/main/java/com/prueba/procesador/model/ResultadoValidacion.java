package com.prueba.procesador.model;

import java.util.List;

/** Resultado de validar una linea: el registro (si es valido) o la lista de errores. */
public record ResultadoValidacion(Registro registro, List<String> errores) {

    public boolean esValido() {
        return errores.isEmpty();
    }
}
