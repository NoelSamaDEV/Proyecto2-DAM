package com.foodnow.backend.entidades;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    @JsonProperty("idCategoria")
    private Integer idCategoria;

    private String nombre;
    private String imagen;
}