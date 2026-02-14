package com.noel.foodnow.models;

import java.util.List;

public class SolicitudPedidoMovil {
    private Integer idMesa;
    private List<ProductoCarrito> productos;

    public SolicitudPedidoMovil(Integer idMesa, List<ProductoCarrito> productos) {
        this.idMesa = idMesa;
        this.productos = productos;
    }

    public static class ProductoCarrito {
        private Integer idProducto;
        private Integer cantidad;

        public ProductoCarrito(Integer idProducto, Integer cantidad) {
            this.idProducto = idProducto;
            this.cantidad = cantidad;
        }
    }
}