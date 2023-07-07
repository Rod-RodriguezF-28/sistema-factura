/*Clientes*/
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES ('Rodrigo', 'Rodriguez', 'rod@gmail.com', '2023-09-17', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES ('Juan', 'Pérez', 'juan.perez@gmail.com', NOW(), '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES ('María', 'García', 'maria.garcia@gmail.com', NOW(), '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES ('Pedro', 'González', 'pedro.gonzalez@gmail.com', NOW(), '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES ('Laura', 'López', 'laura.lopez@gmail.com', NOW(), '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES ('Jorge', 'Hernández', 'jorge.hernandez@gmail.com', NOW(), '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES ('Lucía', 'Fernández', 'lucia.fernandez@gmail.com', NOW(), '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES ('Diego', 'Martínez', 'diego.martinez@gmail.com', NOW(), '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES ('Sofía', 'Sánchez', 'sofia.sanchez@gmail.com', NOW(), '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES ('Carlos', 'Díaz', 'carlos.diaz@gmail.com', NOW(), '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES ('Ana', 'Muñoz', 'ana.munoz@gmail.com', NOW(), '');

/*Productos*/
INSERT INTO productos (nombre, precio, create_at) VALUES ('Panasonic Pantalla LCD', 259999, NOW());
INSERT INTO productos (nombre, precio, create_at) VALUES ('Sony Camara digital DSC-W320B', 123490, NOW());
INSERT INTO productos (nombre, precio, create_at) VALUES ('Apple Ipod shuffle', 1499990, NOW());
INSERT INTO productos (nombre, precio, create_at) VALUES ('Sony Notebook Z110', 37990, NOW());
INSERT INTO productos (nombre, precio, create_at) VALUES ('Hewlett Packard Multifuncional F2280', 69990, NOW());
INSERT INTO productos (nombre, precio, create_at) VALUES ('Bianchi Bicicleta Aro 26', 69990, NOW());
INSERT INTO productos (nombre, precio, create_at) VALUES ('Mica Comoda 5 Cajones', 299990, NOW());

/*Creamos algunas facturas*/
INSERT INTO facturas (descripcion, observacion, cliente_id, create_at) VALUES ('Factura equipos de oficina', null, 1, NOW());
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES (1, 1 ,1);
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES (2, 1 ,4);
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES (1, 1 ,5);
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES (1, 1 ,7);

INSERT INTO facturas (descripcion, observacion, cliente_id, create_at) VALUES ('Factura Bicicleta', 'Alguna nota importante!', 1, NOW());
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES (3, 2 ,6);

/* creamos algunos usuarios con sus roles */
INSERT INTO users (username, password, enable) VALUES ('user', '$2a$10$4K8vQxMTghCOmF4PHR557ulwQCs1d0kG90lUgrpXtqhSUmAeBr4yu',1);
INSERT INTO users (username, password, enable) VALUES ('admin', '$2a$10$J1ugGSHcM6ZphGh2E28P6.4cAue6vrZwmG/Eswl5AOgMfppiTbHIu',1);

INSERT INTO authorities (user_id, authority) VALUES (1,'ROLE_USER');
INSERT INTO authorities (user_id, authority) VALUES (2,'ROLE_ADMIN');
INSERT INTO authorities (user_id, authority) VALUES (2,'ROLE_USER');