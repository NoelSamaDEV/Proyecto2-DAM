package com.foodnow.backend.repositorios;

import com.foodnow.backend.entidades.Pedido; // <--- CORREGIDO A ENTIDADES
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PedidoRepositorio extends JpaRepository<Pedido, Integer> {
    Optional<Pedido> findByMesa_IdMesaAndEstado(Integer idMesa, String estado);
}