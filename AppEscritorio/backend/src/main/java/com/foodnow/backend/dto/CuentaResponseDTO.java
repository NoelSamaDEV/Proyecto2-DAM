package com.foodnow.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class CuentaResponseDTO {
    private List<LineaCuentaDTO> lineas;
    private BigDecimal total;

    public CuentaResponseDTO(List<LineaCuentaDTO> lineas, BigDecimal total) {
        this.lineas = lineas;
        this.total = total;
    }

    // Getters
    public List<LineaCuentaDTO> getLineas() { return lineas; }
    public BigDecimal getTotal() { return total; }

    // Clase interna para las líneas
    public static class LineaCuentaDTO {
        private Integer cantidad;
        private String nombreProducto;
        private BigDecimal subtotal;

        public LineaCuentaDTO(Integer cantidad, String nombreProducto, BigDecimal subtotal) {
            this.cantidad = cantidad;
            this.nombreProducto = nombreProducto;
            this.subtotal = subtotal;
        }

        public Integer getCantidad() { return cantidad; }
        public String getNombreProducto() { return nombreProducto; }
        public BigDecimal getSubtotal() { return subtotal; }
    }
}