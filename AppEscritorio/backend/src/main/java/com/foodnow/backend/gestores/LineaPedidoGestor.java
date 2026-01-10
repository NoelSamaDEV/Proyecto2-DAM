package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.LineaPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineaPedidoGestor extends JpaRepository<LineaPedido, Integer> {
}