package com.prueba.procesador;

import com.prueba.procesador.config.AppConfig;
import com.prueba.procesador.dao.Conexion;
import com.prueba.procesador.dao.EsquemaDao;
import com.prueba.procesador.dao.ProcesamientoDao;
import com.prueba.procesador.dao.RechazoDao;
import com.prueba.procesador.dao.RegistroDao;
import com.prueba.procesador.model.ResumenProcesamiento;
import com.prueba.procesador.service.ProcesadorService;
import com.prueba.procesador.service.ValidadorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.Connection;

/** Uso: mvn exec:java -Dexec.args="data/entrada/clientes_20261007.csv" */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: Main <ruta-del-archivo.csv>");
            System.exit(1);
        }

        try (Connection conexion = Conexion.abrir(new AppConfig())) {
            new EsquemaDao(conexion).crearSiNoExiste();

            ProcesadorService servicio = new ProcesadorService(
                    new ValidadorService(),
                    new RegistroDao(conexion),
                    new RechazoDao(conexion),
                    new ProcesamientoDao(conexion));

            ResumenProcesamiento resumen = servicio.procesar(Path.of(args[0]));
            log.info("Resultado final: {}", resumen);
        } catch (Exception e) {
            log.error("El procesamiento termino con error: {}", e.getMessage());
            System.exit(2);
        }
    }
}
