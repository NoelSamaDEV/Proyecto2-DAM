package com.foodnow.backend.repositorios;

import com.foodnow.backend.modelos.LineaPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineaPedidoRepositorio extends JpaRepository<LineaPedido, Integer> {
    // Este está vacío porque usa las funciones básicas de guardar y borrar
}