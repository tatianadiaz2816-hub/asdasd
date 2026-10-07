CREATE TABLE IF NOT EXISTS procesamiento_archivos (
    id               BIGSERIAL PRIMARY KEY,
    nombre_archivo   VARCHAR(255) NOT NULL,
    fecha_inicio     TIMESTAMP NOT NULL DEFAULT now(),
    fecha_fin        TIMESTAMP,
    estado           VARCHAR(30) NOT NULL,
    total_leidos     INT NOT NULL DEFAULT 0,
    total_validos    INT NOT NULL DEFAULT 0,
    total_invalidos  INT NOT NULL DEFAULT 0,
    total_duplicados INT NOT NULL DEFAULT 0,
    mensaje          TEXT
);

CREATE TABLE IF NOT EXISTS registros_validos (
    id               BIGSERIAL PRIMARY KEY,
    correlation_id   VARCHAR(50) NOT NULL UNIQUE,
    cliente          VARCHAR(100) NOT NULL,
    documento        VARCHAR(15) NOT NULL,
    email            VARCHAR(150),
    monto            NUMERIC(15,2) NOT NULL,
    fecha            DATE NOT NULL,
    procesamiento_id BIGINT NOT NULL REFERENCES procesamiento_archivos(id),
    fecha_carga      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS registros_rechazados (
    id               BIGSERIAL PRIMARY KEY,
    procesamiento_id BIGINT NOT NULL REFERENCES procesamiento_archivos(id),
    numero_linea     INT NOT NULL,
    correlation_id   VARCHAR(50),
    contenido        TEXT NOT NULL,
    motivo           VARCHAR(20) NOT NULL,
    detalle          TEXT NOT NULL,
    fecha_rechazo    TIMESTAMP NOT NULL DEFAULT now()
);
