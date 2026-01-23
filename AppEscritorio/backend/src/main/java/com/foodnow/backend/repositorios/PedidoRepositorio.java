package com.foodnow.backend.repositorios;

import com.foodnow.backend.modelos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PedidoRepositorio extends JpaRepository<Pedido, Integer> {
    // Busca un pedido por el ID de la mesa y el estado (ej: "ABIERTO")
    Optional<Pedido> findByMesa_IdMesaAndEstado(Integer idMesa, String estado);
}