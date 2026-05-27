CREATE TABLE IF NOT EXISTS estudiante (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    correo VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS materia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    creditos INT NOT NULL
);

CREATE TABLE IF NOT EXISTS nota (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    materia_id BIGINT NOT NULL,
    estudiante_id BIGINT NOT NULL,
    observacion VARCHAR(500),
    valor DECIMAL(3,2) NOT NULL,
    porcentaje DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (materia_id) REFERENCES materia(id),
    FOREIGN KEY (estudiante_id) REFERENCES estudiante(id)
);