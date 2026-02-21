package com.noel.foodnow.models;

import com.google.gson.annotations.SerializedName;

public class LineaCuenta {

    @SerializedName("cantidad")
    private Integer cantidad;

    @SerializedName("nombreProducto")
    private String nombreProducto;

    @SerializedName("subtotal")
    private Double subtotal;

    // Getters
    public Integer getCantidad() {
        return cantidad;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public Double getSubtotal() {
        return subtotal;
    }
}