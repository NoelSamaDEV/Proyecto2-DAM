package com.foodnow.backend.entidades;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "linea_pedido")
public class LineaPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_linea")
    private Integer idLinea;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio_unidad")
    private Double precioUnidad;

    @Column(name = "subtotal")
    private Double subtotal; // cantidad * precioUnidad

    // RELACIÓN 1: Pertenece a un pedido general
    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    // RELACIÓN 2: Se refiere a un producto concreto
    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;
}