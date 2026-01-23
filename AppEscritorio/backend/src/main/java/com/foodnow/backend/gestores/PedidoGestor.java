package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.*;
import com.foodnow.backend.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PedidoGestor {

    @Autowired private PedidoInterfaz pedidoInterfaz;
    @Autowired private MesaGestor mesaGestor;
    @Autowired private ProductoInterfaz productoRepo;

    public Optional<Pedido> obtenerPedidoAbierto(Integer idMesa) {
        return pedidoInterfaz.findByMesa_IdMesaAndEstado(idMesa, "ABIERTO");
    }

    public void eliminarPedido(Integer idPedido) {
        pedidoInterfaz.deleteById(idPedido);
    }

    public Pedido anadirProducto(Integer idMesa, Integer idProducto) {
        // 1. Buscamos la mesa (TIENE QUE EXISTIR)
        Mesa mesa = mesaGestor.obtenerPorId(idMesa)
                .orElseThrow(() -> new RuntimeException("Mesa no existe"));

        // 2. Buscamos el producto
        Producto producto = productoRepo.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no existe"));

        // 3. Buscamos pedido abierto o creamos uno nuevo
        Pedido pedido = obtenerPedidoAbierto(idMesa).orElseGet(() -> {
            Pedido nuevo = new Pedido();
            nuevo.setMesa(mesa); // Asignamos la mesa existente
            nuevo.setEstado("ABIERTO");
            nuevo.setFecha(LocalDateTime.now());
            nuevo.setTotal(BigDecimal.ZERO);
            nuevo.setLineasPedido(new ArrayList<>());

            // ACTUALIZAMOS EL ESTADO DE LA MESA APARTE
            mesa.setEstado("OCUPADA");
            mesaGestor.guardarMesa(mesa);

            return pedidoInterfaz.save(nuevo);
        });

        // 4. Crear línea de pedido
        LineaPedido linea = new LineaPedido();
        linea.setPedido(pedido);
        linea.setProducto(producto);
        linea.setCantidad(1);
        linea.setPrecioUnidad(producto.getPrecio());
        linea.setSubtotal(producto.getPrecio());

        // 5. Añadir y Recalcular
        pedido.getLineasPedido().add(linea);

        BigDecimal nuevoTotal = pedido.getLineasPedido().stream()
                .map(LineaPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setTotal(nuevoTotal);

        return pedidoInterfaz.save(pedido);
    }
}