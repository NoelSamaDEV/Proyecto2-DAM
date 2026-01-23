package com.foodnow.backend.entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "linea_pedido")
public class LineaPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_linea")
    private Integer idLinea;

    private Integer cantidad;

    @Column(name = "precio_unidad")
    private BigDecimal precioUnidad;

    private BigDecimal subtotal;

    // --- ESTA PARTE ES CRITICA ---
    @ManyToOne
    @JoinColumn(name = "id_pedido")
    @JsonBackReference // <--- ESTO EVITA QUE SE ROMPA LA LISTA
    private Pedido pedido;
    // -----------------------------

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    // Getters y Setters
    public Integer getIdLinea() { return idLinea; }
    public void setIdLinea(Integer idLinea) { this.idLinea = idLinea; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnidad() { return precioUnidad; }
    public void setPrecioUnidad(BigDecimal precioUnidad) { this.precioUnidad = precioUnidad; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}