-- 0. PREPARACIÓN
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0; 

-- 1. REINICIO TOTAL DE LA BASE DE DATOS
DROP DATABASE IF EXISTS foodnow_db;
CREATE DATABASE foodnow_db;
USE foodnow_db;

-- 2. CREACIÓN DE TABLAS
CREATE TABLE restaurante (
    id_restaurante INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE mesa (
    id_mesa INT AUTO_INCREMENT PRIMARY KEY,
    numero_mesa INT NOT NULL,
    qr_code VARCHAR(255), 
    estado VARCHAR(20) DEFAULT 'LIBRE', 
    id_restaurante INT,
    FOREIGN KEY (id_restaurante) REFERENCES restaurante(id_restaurante)
);

CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    id_restaurante INT,
    FOREIGN KEY (id_restaurante) REFERENCES restaurante(id_restaurante)
);

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

CREATE TABLE pedido (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'ABIERTO', 
    total DECIMAL(10, 2) DEFAULT 0.00,
    id_mesa INT,
    FOREIGN KEY (id_mesa) REFERENCES mesa(id_mesa)
);

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

-- 3. DATOS MAESTROS (Restaurante, Categorías, Productos)
INSERT INTO restaurante (nombre, direccion, telefono, email) 
VALUES ('Restaurante FoodNow', 'Calle Demo 1', '600000000', 'demo@foodnow.com');

INSERT INTO categoria (nombre, id_restaurante) VALUES ('Bebidas', 1); -- ID 1
INSERT INTO categoria (nombre, id_restaurante) VALUES ('Platos', 1);  -- ID 2

INSERT INTO producto (nombre, precio, id_categoria, id_restaurante) VALUES ('Coca-Cola Zero', 2.50, 1, 1); -- ID 1
INSERT INTO producto (nombre, precio, id_categoria, id_restaurante) VALUES ('Hamburguesa', 12.00, 2, 1);   -- ID 2

-- 4. MESAS (Mesa 1 Ocupada, resto Libres)
INSERT INTO mesa (numero_mesa, estado, qr_code, id_restaurante) VALUES (1, 'OCUPADA', 'QR_1', 1);
INSERT INTO mesa (numero_mesa, estado, qr_code, id_restaurante) VALUES (2, 'LIBRE', 'QR_2', 1);
INSERT INTO mesa (numero_mesa, estado, qr_code, id_restaurante) VALUES (3, 'LIBRE', 'QR_3', 1);
INSERT INTO mesa (numero_mesa, estado, qr_code, id_restaurante) VALUES (4, 'LIBRE', 'QR_4', 1);

-- 5. CREACIÓN DEL PEDIDO CORRECTO (17.00€)
-- Creamos el pedido
INSERT INTO pedido (fecha, estado, total, id_mesa) 
VALUES (NOW(), 'ABIERTO', 17.00, 1); -- Vinculado a Mesa 1

-- Capturamos el ID del pedido que acabamos de crear
SET @id_pedido = LAST_INSERT_ID();

-- Insertamos las líneas (¡AQUÍ ESTÁ LA CORRECCIÓN!)
-- 2 Coca-Colas (2 x 2.50 = 5.00)
INSERT INTO linea_pedido (cantidad, precio_unidad, subtotal, id_pedido, id_producto) 
VALUES (2, 2.50, 5.00, @id_pedido, 1); 

-- 1 Hamburguesa (1 x 12.00 = 12.00)
INSERT INTO linea_pedido (cantidad, precio_unidad, subtotal, id_pedido, id_producto) 
VALUES (1, 12.00, 12.00, @id_pedido, 2);

-- Total comprobado: 5.00 + 12.00 = 17.00 (Coincide con el pedido)

-- 6. FINALIZAR
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

-- Verificación visual: Tienen que salir 2 filas
SELECT * FROM linea_pedido;