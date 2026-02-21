package com.noel.foodnow.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarritoManager {
    private static CarritoManager instance;
    private Integer idMesaActual;
    private List<ProductoPedido> productosEnCarrito;

    private CarritoManager() {
        productosEnCarrito = new ArrayList<>();
    }

    public static synchronized CarritoManager getInstance() {
        if (instance == null) {
            instance = new CarritoManager();
        }
        return instance;
    }

    public void setIdMesa(Integer id) { this.idMesaActual = id; }
    public Integer getIdMesa() { return idMesaActual; }

    public void agregarProducto(Producto producto, int cantidad) {
        for (ProductoPedido p : productosEnCarrito) {
            if (p.getProducto().getIdProducto().equals(producto.getIdProducto())) {
                p.sumarCantidad(cantidad);
                return;
            }
        }
        productosEnCarrito.add(new ProductoPedido(producto, cantidad));
    }

    // --- NUEVO: RESTAR CANTIDAD ---
    public void restarCantidad(Producto producto) {
        for (int i = 0; i < productosEnCarrito.size(); i++) {
            ProductoPedido p = productosEnCarrito.get(i);
            if (p.getProducto().getIdProducto().equals(producto.getIdProducto())) {
                if (p.getCantidad() > 1) {
                    p.sumarCantidad(-1);
                } else {
                    productosEnCarrito.remove(i); // Si llega a 0, se borra
                }
                return;
            }
        }
    }

    // --- NUEVO: ELIMINAR POR COMPLETO ---
    public void eliminarProducto(Producto producto) {
        for (int i = 0; i < productosEnCarrito.size(); i++) {
            if (productosEnCarrito.get(i).getProducto().getIdProducto().equals(producto.getIdProducto())) {
                productosEnCarrito.remove(i);
                return;
            }
        }
    }

    public List<ProductoPedido> getCarrito() { return productosEnCarrito; }

    public void vaciarCarrito() {
        productosEnCarrito.clear();
    }

    public double calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ProductoPedido p : productosEnCarrito) {
            BigDecimal subtotal = p.getProducto().getPrecio().multiply(new BigDecimal(p.getCantidad()));
            total = total.add(subtotal);
        }
        return total.doubleValue();
    }

    public static class ProductoPedido {
        private Producto producto;
        private int cantidad;

        public ProductoPedido(Producto p, int c) { this.producto = p; this.cantidad = c; }
        public void sumarCantidad(int c) { this.cantidad += c; }
        public Producto getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
    }
}