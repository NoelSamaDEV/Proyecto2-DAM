package com.foodnow.backend.interfaces;

import com.foodnow.backend.entidades.LineaPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineaPedidoInterfaz extends JpaRepository<LineaPedido, Integer> {
}