package com.foodnow.backend.entidades;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    @Column(name = "fecha")
    private LocalDateTime fecha; // Guarda fecha y hora automáticamente

    @Column(name = "estado")
    private String estado; // "ABIERTO", "CERRADO", "PAGADO"

    @Column(name = "total")
    private Double total;

    // RELACIÓN: Muchos pedidos se hacen en una mesa
    @ManyToOne
    @JoinColumn(name = "id_mesa")
    private Mesa mesa;
}