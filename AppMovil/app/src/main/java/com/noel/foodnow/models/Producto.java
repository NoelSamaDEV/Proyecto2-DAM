package com.noel.foodnow.models;

import java.io.Serializable;

public class Producto implements Serializable {
    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagenUrl;

    // Getters
    public Integer getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Double getPrecio() { return precio; }
    public String getImagenUrl() { return imagenUrl; }
}