package com.foodnow.backend.entidades;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "mesa")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mesa")
    private Integer idMesa;

    @Column(name = "numero_mesa")
    private Integer numeroMesa;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "estado")
    private String estado;

    // RELACIÓN: Muchas mesas -> Un restaurante
    @ManyToOne
    @JoinColumn(name = "id_restaurante") // Clave foránea que referencia a Restaurante
    private Restaurante restaurante;
}