package com.foodnow.backend.interfaces;

import com.foodnow.backend.entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoInterfaz extends JpaRepository<Pedido, Integer> {

    // 1. Para buscar el pedido que está comiendo una mesa ahora mismo
    Optional<Pedido> findByMesa_IdMesaAndEstado(Integer idMesa, String estado);

    // 2. Para buscar todos los tickets cerrados (Historial)
    List<Pedido> findByEstado(String estado);
}