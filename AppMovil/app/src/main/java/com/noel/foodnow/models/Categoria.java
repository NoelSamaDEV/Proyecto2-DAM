package com.noel.foodnow.models;
import com.google.gson.annotations.SerializedName;

public class Categoria {
    @SerializedName("idCategoria")
    private Integer idCategoria;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("imagen")
    private String imagen;

    public Integer getIdCategoria() { return idCategoria; }
    public String getNombre() { return nombre; }
    public String getImagen() { return imagen; }
}