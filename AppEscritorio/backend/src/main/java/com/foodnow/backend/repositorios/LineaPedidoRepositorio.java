package com.foodnow.backend.repositorios;

import com.foodnow.backend.entidades.LineaPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.Optional;

public interface LineaPedidoRepositorio extends JpaRepository<LineaPedido, Integer> {

    // Busca si existe una línea (para sumar cantidad)
    Optional<LineaPedido> findByPedido_IdPedidoAndProducto_IdProducto(Integer idPedido, Integer idProducto);

    // SUMA INFALIBLE: Calcula el total directamente en la base de datos
    @Query("SELECT COALESCE(SUM(l.subtotal), 0) FROM LineaPedido l WHERE l.pedido.idPedido = :idPedido")
    BigDecimal calcularTotalPedido(@Param("idPedido") Integer idPedido);
}