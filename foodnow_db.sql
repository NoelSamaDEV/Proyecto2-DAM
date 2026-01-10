CREATE DATABASE IF NOT EXISTS foodnow_db;
USE foodnow_db;

-- Tabla RESTAURANTE
CREATE TABLE restaurante (
    id_restaurante INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(20),
    email VARCHAR(100)
);

-- Tabla MESA 
CREATE TABLE mesa (
    id_mesa INT AUTO_INCREMENT PRIMARY KEY,
    numero_mesa INT NOT NULL,
    qr_code VARCHAR(255), 
    estado VARCHAR(20) DEFAULT 'LIBRE', -- LIBRE, OCUPADA, PIDIENDO_CUENTA
    id_restaurante INT,
    FOREIGN KEY (id_restaurante) REFERENCES restaurante(id_restaurante)
);

-- Tabla CATEGORIA (Para organizar el menú)
CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    id_restaurante INT,
    FOREIGN KEY (id_restaurante) REFERENCES restaurante(id_restaurante)
);

-- Tabla PRODUCTO 
CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL, 
    imagen VARCHAR(255), 
    id_restaurante INT,
    id_categoria INT,
    FOREIGN KEY (id_restaurante) REFERENCES restaurante(id_restaurante),
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

-- Tabla PEDIDO
CREATE TABLE pedido (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'ABIERTO', -- ABIERTO, ENVIADO, CERRADO
    total DECIMAL(10, 2) DEFAULT 0.00,
    id_mesa INT,
    FOREIGN KEY (id_mesa) REFERENCES mesa(id_mesa)
);

-- Tabla LINEA_PEDIDO 
CREATE TABLE linea_pedido (
    id_linea INT AUTO_INCREMENT PRIMARY KEY,
    cantidad INT NOT NULL,
    precio_unidad DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    id_pedido INT,
    id_producto INT,
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

-- Restaurante de prueba para empezar
INSERT INTO restaurante (nombre, direccion, telefono, email) 
VALUES ('Restaurante FoodNow Demo', 'Calle Principal 1', '600123456', 'contacto@foodnow.com');