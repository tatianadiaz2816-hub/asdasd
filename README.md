# procesador-archivos

Procesa archivos CSV recibidos por GoAnywhere/SFTP: valida estructura y datos, controla duplicados por
`correlation_id` y guarda en PostgreSQL. Los rechazos y el resumen de cada corrida quedan registrados.

## Formato del archivo

Cabecera obligatoria: `correlation_id,cliente,documento,email,monto,fecha`

| Campo | Regla |
|---|---|
| correlation_id | obligatorio, letras/numeros/guion, max 50, unico |
| cliente | obligatorio, max 100 |
| documento | obligatorio, 5 a 15 digitos |
| email | opcional; si viene, formato valido |
| monto | obligatorio, mayor a 0, max 2 decimales |
| fecha | obligatoria, `yyyy-MM-dd`, no futura |

## Estructura

- `model`: registros y resultados
- `dao`: acceso a PostgreSQL (JDBC)
- `service`: validacion y orquestacion
- `config`, `exception`

## Base de datos

```sql
CREATE DATABASE pruebatecnica;
```

Las tablas se crean solas al ejecutar (`schema.sql`): `procesamiento_archivos`, `registros_validos`, `registros_rechazados`.

## Ejecutar

```powershell
$env:DB_PASSWORD = "su_clave"
mvn test
mvn compile exec:java "-Dexec.args=data/entrada/clientes_20261007.csv"
```

Resultado esperado con el archivo de ejemplo: 12 leidos, 4 validos, 7 invalidos, 1 duplicado.
Al ejecutarlo una segunda vez, los 4 validos pasan a duplicados (ya existen en la base de datos).

## Verificar en DBeaver

```sql
SELECT * FROM procesamiento_archivos ORDER BY id DESC;
SELECT * FROM registros_validos;
SELECT numero_linea, correlation_id, motivo, detalle FROM registros_rechazados ORDER BY id;
```
