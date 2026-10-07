package com.prueba.procesador.model;

public class ResumenProcesamiento {

    private int leidos;
    private int validos;
    private int invalidos;
    private int duplicados;

    public void sumarLeido() {
        leidos++;
    }

    public void sumarValido() {
        validos++;
    }

    public void sumarInvalido() {
        invalidos++;
    }

    public void sumarDuplicado() {
        duplicados++;
    }

    public int getLeidos() {
        return leidos;
    }

    public int getValidos() {
        return validos;
    }

    public int getInvalidos() {
        return invalidos;
    }

    public int getDuplicados() {
        return duplicados;
    }

    @Override
    public String toString() {
        return "leidos=" + leidos + ", validos=" + validos
                + ", invalidos=" + invalidos + ", duplicados=" + duplicados;
    }
}
