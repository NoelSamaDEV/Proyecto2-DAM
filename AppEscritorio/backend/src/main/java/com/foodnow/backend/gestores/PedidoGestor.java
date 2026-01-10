package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoGestor extends JpaRepository<Pedido, Integer> {
}