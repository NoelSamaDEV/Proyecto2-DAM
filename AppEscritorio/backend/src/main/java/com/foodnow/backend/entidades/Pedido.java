package com.foodnow.backend.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    private LocalDateTime fecha;
    private String estado;
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "id_mesa")
    private Mesa mesa;

    // IMPORTANTE: EAGER para cargar siempre y ArrayList para no tener nulos
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("pedido")
    private List<LineaPedido> lineasPedido = new ArrayList<>();

    // GETTERS Y SETTERS
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }

    public List<LineaPedido> getLineasPedido() { return lineasPedido; }
    public void setLineasPedido(List<LineaPedido> lineasPedido) { this.lineasPedido = lineasPedido; }
}