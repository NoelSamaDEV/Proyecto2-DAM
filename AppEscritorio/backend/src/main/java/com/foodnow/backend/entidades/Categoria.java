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
    @Column(name = "id_categoria") // Como se llama en MySQL
    @JsonProperty("idCategoria")   // Como se enviará en el JSON
    private Integer idCategoria;   // Ya no se llama idMesa

    private String nombre;
    private String imagen;
}