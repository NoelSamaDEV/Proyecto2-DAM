-- 0. PREPARACIÓN
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0; 

-- 1. REINICIO TOTAL DE LA BASE DE DATOS
DROP DATABASE IF EXISTS foodnow_db;
CREATE DATABASE foodnow_db;
USE foodnow_db;

-- 2. CREACIÓN DE TABLAS (Ajustadas para la App Móvil)
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
    imagen VARCHAR(255),
    id_restaurante INT,
    FOREIGN KEY (id_restaurante) REFERENCES restaurante(id_restaurante)
);

CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(10, 2) NOT NULL, 
    imagen VARCHAR(255), 
    id_restaurante INT,
    id_categoria INT,
    FOREIGN KEY (id_restaurante) REFERENCES restaurante(id_restaurante),
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

CREATE TABLE pedido (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME,
    estado VARCHAR(50),
    total DECIMAL(10,2),
    id_mesa INT,
    FOREIGN KEY (id_mesa) REFERENCES mesa(id_mesa)
);

CREATE TABLE linea_pedido (
    id_linea INT AUTO_INCREMENT PRIMARY KEY,
    cantidad INT,
    precio_unidad DECIMAL(10,2),
    subtotal DECIMAL(10,2),
    id_pedido INT,
    id_producto INT,
    servido TINYINT(1) DEFAULT 0,
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

-- 3. INSERCIÓN DE DATOS DE PRUEBA

-- Restaurante
INSERT INTO restaurante (nombre, direccion, telefono, email) 
VALUES ('FoodNow Burger', 'Calle Principal 123', '910000000', 'info@foodnow.com');

-- Categorías (Con iconos)
INSERT INTO categoria (nombre, imagen, id_restaurante) VALUES 
('Hamburguesas', 'https://cdn-icons-png.flaticon.com/512/3075/3075977.png', 1),
('Entrantes', 'https://cdn-icons-png.flaticon.com/512/3480/3480823.png', 1),
('Bebidas', 'https://cdn-icons-png.flaticon.com/512/2738/2738730.png', 1),
('Postres', 'https://cdn-icons-png.flaticon.com/512/3173/3173420.png', 1);

-- Productos (8 por categoría con imágenes funcionales de Unsplash)
INSERT INTO producto (nombre, descripcion, precio, imagen, id_restaurante, id_categoria) VALUES 
-- 🍔 CATEGORÍA 1: HAMBURGUESAS
('Burger Clásica', 'Carne de vacuno 100%, queso cheddar, lechuga y tomate.', 9.50, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500&q=80', 1, 1),
('Bacon Cheeseburger', 'Doble ración de bacon crujiente, queso fundido y salsa BBQ.', 11.00, 'https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?w=500&q=80', 1, 1),
('Burger BBQ', 'Doble carne, aros de cebolla, bacon y salsa barbacoa.', 12.50, 'https://images.unsplash.com/photo-1550547660-d9450f859349?w=500&q=80', 1, 1),
('Veggie Burger', 'Medallón de garbanzos, aguacate, tomate y cebolla morada.', 10.00, 'https://images.unsplash.com/photo-1520072959219-c595dc870360?w=500&q=80', 1, 1),
('Chicken Burger', 'Pechuga de pollo crujiente, lechuga, tomate y mayonesa.', 9.00, 'https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=500&q=80', 1, 1),
('Truffle Burger', 'Carne de vacuno, queso trufado, setas y rúcula.', 13.00, 'https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=500&q=80', 1, 1),
('Smash Burger', 'Doble carne aplastada, doble cheddar y pan brioche.', 10.50, 'https://images.unsplash.com/photo-1608767221051-2b9d18f35a2f?w=500&q=80', 1, 1),
('Spicy Burger', 'Carne, jalapeños, salsa picante y queso Monterrey Jack.', 11.50, 'https://images.unsplash.com/photo-1553979459-d2229ba7433b?w=500&q=80', 1, 1),

-- 🍟 CATEGORÍA 2: ENTRANTES
('Patatas Fritas', 'Ración grande de patatas crujientes.', 3.50, 'https://images.unsplash.com/photo-1630384060421-cb20d0e0649d?w=500&q=80', 1, 2),
('Nachos con Queso', 'Nachos bañados en queso fundido, guacamole y jalapeños.', 7.00, 'https://images.unsplash.com/photo-1513456852971-30c0b8199d4d?w=500&q=80', 1, 2),
('Alitas BBQ', '6 alitas de pollo bañadas en salsa barbacoa.', 6.50, 'https://images.unsplash.com/photo-1567620832903-9fc6debc209f?w=500&q=80', 1, 2),
('Aros de Cebolla', 'Crujientes aros de cebolla rebozados con salsa.', 4.50, 'https://images.unsplash.com/photo-1625938146369-adc83368bda7?w=500&q=80', 1, 2),
('Tequeños', 'Palitos de queso envueltos en masa crujiente (5 uds).', 6.00, 'https://images.unsplash.com/photo-1615719413546-198b25453f85?w=500&q=80', 1, 2),
('Fingers de Pollo', 'Tiras de pollo empanado con salsa mostaza miel.', 5.50, 'https://images.unsplash.com/photo-1562967914-01efa7e87832?w=500&q=80', 1, 2),
('Ensalada César', 'Lechuga romana, pollo asado, picatostes y salsa César.', 7.50, 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=500&q=80', 1, 2),
('Patatas Bravas', 'Patatas en dados con nuestra salsa brava casera.', 4.00, 'https://images.unsplash.com/photo-1541592106381-b31e9677c0e5?w=500&q=80', 1, 2),

-- 🥤 CATEGORÍA 3: BEBIDAS
('Coca-Cola', 'Lata de 33cl bien fría.', 2.50, 'https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=500&q=80', 1, 3),
('Agua Mineral', 'Botella de agua de 50cl.', 1.50, 'https://images.unsplash.com/photo-1523362628745-0c100150b504?w=500&q=80', 1, 3),
('Cerveza Artesana', 'Cerveza rubia de barril.', 3.00, 'https://images.unsplash.com/photo-1535958636474-b021ee887b13?w=500&q=80', 1, 3),
('Fanta Naranja', 'Lata de Fanta de 33cl.', 2.50, 'https://images.unsplash.com/photo-1624517452488-04869289c4ca?w=500&q=80', 1, 3),
('Nestea', 'Té helado al limón.', 2.50, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=500&q=80', 1, 3),
('Zumo de Naranja', 'Zumo natural recién exprimido.', 3.50, 'https://images.unsplash.com/photo-1613478223719-2ab802602423?w=500&q=80', 1, 3),
('Limonada Casera', 'Limonada fresca con menta.', 3.00, 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=500&q=80', 1, 3),
('Vino Tinto', 'Copa de vino tinto de la casa.', 3.50, 'https://images.unsplash.com/photo-1506377247377-2a5b3b417ebb?w=500&q=80', 1, 3),

-- 🍰 CATEGORÍA 4: POSTRES
('Tarta de Queso', 'Cheesecake casero con coulis de frutos rojos.', 5.50, 'https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=500&q=80', 1, 4),
('Brownie de Chocolate', 'Brownie caliente con nueces y bola de helado.', 6.00, 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&q=80', 1, 4),
('Flan de Caramelo', 'Flan casero tradicional bañado en suave caramelo líquido.', 5.50, 'https://images.unsplash.com/photo-1587314168485-3236d6710814?w=500&q=80', 1, 4),
('Coulant de Chocolate', 'Bizcocho relleno de chocolate caliente fundido.', 6.50, 'https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=500&q=80', 1, 4),
('Tiramisú', 'Postre italiano clásico con mascarpone y café.', 5.00, 'https://images.unsplash.com/photo-1571115177098-24ec42ed204d?w=500&q=80', 1, 4),
('Batido de Fresa', 'Milkshake de fresa con nata montada.', 4.50, 'https://images.unsplash.com/photo-1553177595-4de2bb0842b9?w=500&q=80', 1, 4),
('Crepes de Nutella', 'Crepes recién hechos rellenos de crema de cacao.', 5.00, 'https://images.unsplash.com/photo-1519676867240-f03562e64548?w=500&q=80', 1, 4);

-- Mesas
INSERT INTO mesa (numero_mesa, estado, qr_code, id_restaurante) VALUES 
(1, 'LIBRE', 'https://foodnow.app/mesa/1', 1),
(2, 'LIBRE', 'https://foodnow.app/mesa/2', 1),
(3, 'LIBRE', 'https://foodnow.app/mesa/3', 1),
(4, 'LIBRE', 'https://foodnow.app/mesa/4', 1);

-- Reactivamos las claves foráneas
SET FOREIGN_KEY_CHECKS = 1;