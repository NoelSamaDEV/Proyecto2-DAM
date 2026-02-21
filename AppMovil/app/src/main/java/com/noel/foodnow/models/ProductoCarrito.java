package com.noel.foodnow.models;

import com.google.gson.annotations.SerializedName;

public class ProductoCarrito {

    @SerializedName("idProducto")
    private Integer idProducto;

    @SerializedName("cantidad")
    private Integer cantidad;

    // Constructor que usamos en CarritoActivity
    public ProductoCarrito(Integer idProducto, Integer cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}