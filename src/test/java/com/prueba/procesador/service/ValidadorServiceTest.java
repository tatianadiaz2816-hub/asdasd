package com.prueba.procesador.service;

import com.prueba.procesador.model.ResultadoValidacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorServiceTest {

    private final ValidadorService validador = new ValidadorService();

    private static String[] linea(String texto) {
        return texto.split(",", -1);
    }

    @Test
    void aceptaRegistroCorrecto() {
        ResultadoValidacion r = validador.validar(linea("COR-1,Ana Torres,10203040,ana@correo.com,150000.50,2026-10-01"));
        assertTrue(r.esValido());
        assertEquals("COR-1", r.registro().correlationId());
    }

    @Test
    void emailEsOpcional() {
        ResultadoValidacion r = validador.validar(linea("COR-3,Marta,30405060,,45000.75,2026-10-03"));
        assertTrue(r.esValido());
        assertNull(r.registro().email());
    }

    @Test
    void rechazaCamposObligatoriosVacios() {
        ResultadoValidacion r = validador.validar(linea(",,,,,"));
        assertFalse(r.esValido());
        assertEquals(5, r.errores().size());
    }

    @Test
    void rechazaCantidadDeColumnasIncorrecta() {
        ResultadoValidacion r = validador.validar(linea("COR-9,Elena,90102030,elena@correo.com,99000"));
        assertFalse(r.esValido());
    }

    @Test
    void rechazaDatosConFormatoInvalido() {
        assertFalse(validador.validar(linea("COR-5,Pedro,ABC123,p@correo.com,12000,2026-10-05")).esValido());
        assertFalse(validador.validar(linea("COR-6,Sofia,50607080,sofia-correo.com,32000,2026-10-06")).esValido());
        assertFalse(validador.validar(linea("COR-7,Juan,60708090,j@correo.com,-500,2026-10-06")).esValido());
        assertFalse(validador.validar(linea("COR-8,Laura,70809010,l@correo.com,64000,2026-13-45")).esValido());
    }

    @Test
    void rechazaFechaFutura() {
        assertFalse(validador.validar(linea("COR-1,Ana,10203040,a@correo.com,100,2999-01-01")).esValido());
    }
}
