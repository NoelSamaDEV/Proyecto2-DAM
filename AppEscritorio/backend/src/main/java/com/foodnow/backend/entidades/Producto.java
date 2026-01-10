package com.foodnow.backend.entidades;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "precio")
    private Double precio; // Uso Double para representar precios con decimales

    @Column(name = "imagen")
    private String imagen; // Aquí se guarda la URL o nombre del archivo

    // RELACIÓN 1: Un producto es de un restaurante
    @ManyToOne
    @JoinColumn(name = "id_restaurante")
    private Restaurante restaurante;

    // RELACIÓN 2: Un producto pertenece a una categoría
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
}