package com.noel.foodnow.models;

import com.google.gson.annotations.SerializedName;

public class Categoria {

    @SerializedName("id_categoria")
    private Integer idCategoria;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("imagen")
    private String imagen;

    // CONSTRUCTOR
    public Categoria() {}

    // GETTERS Y SETTERS
    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}