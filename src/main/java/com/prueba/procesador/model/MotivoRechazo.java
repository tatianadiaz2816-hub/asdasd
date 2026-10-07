package com.prueba.procesador.model;

public enum MotivoRechazo {
    /** Estructura o datos incorrectos. */
    INVALIDO,
    /** El correlation_id ya existe, en el mismo archivo o en la base de datos. */
    DUPLICADO
}
