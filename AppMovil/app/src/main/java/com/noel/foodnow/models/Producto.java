package com.noel.foodnow.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class Producto {
    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagen;

    public Producto() {}

    // Getters y Setters
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}