package com.noel.foodnow.models;

import java.util.ArrayList;
import java.util.List;

public class CarritoManager {
    private static CarritoManager instance;

    private Integer idMesaActual;
    private List<ProductoPedido> productosEnCarrito;

    // Constructor privado
    private CarritoManager() {
        productosEnCarrito = new ArrayList<>();
    }

    // Método para obtener la mochila
    public static synchronized CarritoManager getInstance() {
        if (instance == null) {
            instance = new CarritoManager();
        }
        return instance;
    }

    // --- MÉTODOS DE LA MESA ---
    public void setIdMesa(Integer id) { this.idMesaActual = id; }
    public Integer getIdMesa() { return idMesaActual; }

    // --- MÉTODOS DEL CARRITO ---
    public void agregarProducto(Producto producto, int cantidad) {
        for (ProductoPedido p : productosEnCarrito) {
            if (p.getProducto().getIdProducto().equals(producto.getIdProducto())) {
                p.sumarCantidad(cantidad);
                return;
            }
        }
        productosEnCarrito.add(new ProductoPedido(producto, cantidad));
    }

    public List<ProductoPedido> getCarrito() { return productosEnCarrito; }

    public void vaciarCarrito() {
        productosEnCarrito.clear();
        idMesaActual = null;
    }

    public double calcularTotal() {
        double total = 0;
        for (ProductoPedido p : productosEnCarrito) {
            total += p.getProducto().getPrecio() * p.getCantidad();
        }
        return total;
    }

    // CLASE INTERNA AUXILIAR
    public static class ProductoPedido {
        private Producto producto;
        private int cantidad;

        public ProductoPedido(Producto p, int c) { this.producto = p; this.cantidad = c; }
        public void sumarCantidad(int c) { this.cantidad += c; }
        public Producto getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
    }
}