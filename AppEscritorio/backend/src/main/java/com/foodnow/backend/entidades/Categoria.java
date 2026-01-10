package com.foodnow.backend.entidades;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "nombre")
    private String nombre;

    // RELACIÓN: Muchas categorías -> Un restaurante
    @ManyToOne
    @JoinColumn(name = "id_restaurante")
    private Restaurante restaurante;
}