package com.foodnow.backend.controladores;

import com.foodnow.backend.entidades.*;
import com.foodnow.backend.repositorios.*;
import com.foodnow.backend.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoControlador {

    @Autowired private PedidoRepositorio pedidoRepo;
    @Autowired private MesaInterfaz mesaRepo;
    @Autowired private ProductoInterfaz productoRepo;
    @Autowired private LineaPedidoRepositorio lineaRepo;

    // --- ENDPOINT DE NOTIFICACIONES (POLLING) ---
    // Este método devuelve SOLO los pedidos que están ABIERTOS.
    // La app de escritorio llamará aquí cada 3 segundos.
    @GetMapping("/pendientes")
    public ResponseEntity<List<Pedido>> obtenerPedidosPendientes() {
        List<Pedido> todos = pedidoRepo.findAll();
        List<Pedido> pendientes = new ArrayList<>();

        for (Pedido p : todos) {
            if ("ABIERTO".equals(p.getEstado())) {
                // Truco de seguridad: Recalculamos el total real antes de enviarlo
                BigDecimal totalReal = lineaRepo.calcularTotalPedido(p.getIdPedido());
                p.setTotal(totalReal);
                pendientes.add(p);
            }
        }
        return ResponseEntity.ok(pendientes);
    }
    // ---------------------------------------------

    @GetMapping("/mesa/{idMesa}/actual")
    public ResponseEntity<Pedido> obtenerPedidoActual(@PathVariable Integer idMesa) {
        Optional<Pedido> pedidoOpt = pedidoRepo.findByMesa_IdMesaAndEstado(idMesa, "ABIERTO");
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            BigDecimal totalReal = lineaRepo.calcularTotalPedido(pedido.getIdPedido());
            // Si hay discrepancia, corregimos en BD
            if (pedido.getTotal().compareTo(totalReal) != 0) {
                pedido.setTotal(totalReal);
                pedidoRepo.save(pedido);
            }
            return ResponseEntity.ok(pedido);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/mesa/{idMesa}/agregar")
    public ResponseEntity<Map<String, Object>> agregarProducto(
            @PathVariable Integer idMesa,
            @RequestBody SolicitudProducto solicitud) {

        Map<String, Object> respuesta = new HashMap<>();
        Mesa mesa = mesaRepo.findById(idMesa).orElse(null);
        if (mesa == null) return ResponseEntity.notFound().build();

        // LOGICA DE "FUERZA BRUTA" PARA EL ESTADO
        if (!"OCUPADA".equals(mesa.getEstado())) {
            // Si parecía libre, limpiamos pedidos zombies anteriores
            Optional<Pedido> pedidoZombie = pedidoRepo.findByMesa_IdMesaAndEstado(idMesa, "ABIERTO");
            if (pedidoZombie.isPresent()) {
                Pedido viejo = pedidoZombie.get();
                viejo.setEstado("CERRADO");
                pedidoRepo.saveAndFlush(viejo);
            }
            // Forzamos estado OCUPADA
            mesaRepo.forzarEstadoOcupada(idMesa);
            mesa.setEstado("OCUPADA");
            respuesta.put("mensaje", "Mesa abierta y pedido iniciado.");
        } else {
            respuesta.put("mensaje", "Producto sumado al pedido.");
        }

        // Obtener o crear pedido
        Pedido pedido = pedidoRepo.findByMesa_IdMesaAndEstado(idMesa, "ABIERTO")
                .orElseGet(() -> {
                    Pedido p = new Pedido();
                    p.setMesa(mesa);
                    p.setEstado("ABIERTO");
                    p.setFecha(LocalDateTime.now());
                    p.setTotal(BigDecimal.ZERO);
                    p.setLineasPedido(new ArrayList<>());
                    return pedidoRepo.saveAndFlush(p);
                });

        // Añadir producto
        Producto producto = productoRepo.findById(solicitud.idProducto).orElse(null);
        if (producto != null) {
            Optional<LineaPedido> lineaExistente = lineaRepo.findByPedido_IdPedidoAndProducto_IdProducto(
                    pedido.getIdPedido(), producto.getIdProducto());

            if (lineaExistente.isPresent()) {
                LineaPedido linea = lineaExistente.get();
                int nuevaCantidad = linea.getCantidad() + solicitud.cantidad;
                linea.setCantidad(nuevaCantidad);
                linea.setSubtotal(producto.getPrecio().multiply(new BigDecimal(nuevaCantidad)));
                lineaRepo.saveAndFlush(linea);
            } else {
                LineaPedido nueva = new LineaPedido();
                nueva.setPedido(pedido);
                nueva.setProducto(producto);
                nueva.setCantidad(solicitud.cantidad);
                nueva.setPrecioUnidad(producto.getPrecio());
                nueva.setSubtotal(producto.getPrecio().multiply(new BigDecimal(solicitud.cantidad)));
                lineaRepo.saveAndFlush(nueva);
            }

            // Recalcular total final y devolver
            BigDecimal totalCalculado = lineaRepo.calcularTotalPedido(pedido.getIdPedido());
            pedido.setTotal(totalCalculado);
            pedidoRepo.saveAndFlush(pedido);

            Pedido pedidoFinal = pedidoRepo.findById(pedido.getIdPedido()).get();
            respuesta.put("pedido", pedidoFinal);
            respuesta.put("status", "success");
            return ResponseEntity.ok(respuesta);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/mesa/{idMesa}/cerrar")
    public ResponseEntity<?> cerrarMesa(@PathVariable Integer idMesa) {
        Optional<Pedido> pedidoOpt = pedidoRepo.findByMesa_IdMesaAndEstado(idMesa, "ABIERTO");
        if (pedidoOpt.isPresent()) {
            Pedido p = pedidoOpt.get();
            p.setEstado("CERRADO");
            pedidoRepo.save(p);
        }
        mesaRepo.forzarEstadoLibre(idMesa);
        return ResponseEntity.ok().build();
    }

    public static class SolicitudProducto {
        public Integer idProducto;
        public Integer cantidad;
    }
}