package com.noel.foodnow.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SolicitudPedidoMovil {

    @SerializedName("idMesa")
    private Integer idMesa;

    @SerializedName("productos")
    private List<ProductoCarrito> productos;

    // Constructor vacío por si acaso lo necesita Retrofit
    public SolicitudPedidoMovil() {
    }

    // Constructor que usamos en CarritoActivity
    public SolicitudPedidoMovil(Integer idMesa, List<ProductoCarrito> productos) {
        this.idMesa = idMesa;
        this.productos = productos;
    }

    // Getters y Setters
    public Integer getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(Integer idMesa) {
        this.idMesa = idMesa;
    }

    public List<ProductoCarrito> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoCarrito> productos) {
        this.productos = productos;
    }
}