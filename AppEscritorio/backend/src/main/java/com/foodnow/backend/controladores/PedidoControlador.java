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

    @GetMapping("/pendientes")
    public ResponseEntity<List<Pedido>> obtenerPedidosPendientes() {
        List<Pedido> todos = pedidoRepo.findAll();
        List<Pedido> pendientes = new ArrayList<>();

        for (Pedido p : todos) {
            if ("ABIERTO".equals(p.getEstado())) {
                BigDecimal totalReal = lineaRepo.calcularTotalPedido(p.getIdPedido());
                p.setTotal(totalReal);
                pendientes.add(p);
            }
        }
        return ResponseEntity.ok(pendientes);
    }

    @GetMapping("/mesa/{idMesa}/actual")
    public ResponseEntity<Pedido> obtenerPedidoActual(@PathVariable Integer idMesa) {
        Optional<Pedido> pedidoOpt = pedidoRepo.findByMesa_IdMesaAndEstado(idMesa, "ABIERTO");
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            BigDecimal totalReal = lineaRepo.calcularTotalPedido(pedido.getIdPedido());
            if (pedido.getTotal().compareTo(totalReal) != 0) {
                pedido.setTotal(totalReal);
                pedidoRepo.saveAndFlush(pedido);
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

        if (!"OCUPADA".equals(mesa.getEstado())) {
            Optional<Pedido> pedidoZombie = pedidoRepo.findByMesa_IdMesaAndEstado(idMesa, "ABIERTO");
            if (pedidoZombie.isPresent()) {
                Pedido viejo = pedidoZombie.get();
                viejo.setEstado("CERRADO");
                pedidoRepo.saveAndFlush(viejo);
            }

            mesaRepo.forzarEstadoOcupada(idMesa);
            mesa.setEstado("OCUPADA");
            respuesta.put("mensaje", "Mesa abierta y pedido iniciado.");
        } else {
            respuesta.put("mensaje", "Producto sumado al pedido.");
        }

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

        Producto producto = productoRepo.findById(solicitud.idProducto).orElse(null);
        if (producto != null) {

            // 🛠️ LA MAGIA ESTÁ AQUÍ: Buscamos si ya hay una línea de este producto... pero SOLO SI AÚN NO SE HA SERVIDO
            Optional<LineaPedido> lineaExistente = pedido.getLineasPedido().stream()
                    .filter(l -> l.getProducto().getIdProducto().equals(producto.getIdProducto()) && !l.getServido())
                    .findFirst();

            if (lineaExistente.isPresent()) {
                // Si la cocina aún no lo ha hecho, se lo sumamos a la cantidad actual
                LineaPedido linea = lineaExistente.get();
                int nuevaCantidad = linea.getCantidad() + solicitud.cantidad;
                linea.setCantidad(nuevaCantidad);
                linea.setSubtotal(producto.getPrecio().multiply(new BigDecimal(nuevaCantidad)));
                lineaRepo.saveAndFlush(linea);
            } else {
                // Si la cocina ya lo sirvió (o es la primera vez), creamos una línea totalmente nueva
                LineaPedido nueva = new LineaPedido();
                nueva.setPedido(pedido);
                nueva.setProducto(producto);
                nueva.setCantidad(solicitud.cantidad);
                nueva.setPrecioUnidad(producto.getPrecio());
                nueva.setSubtotal(producto.getPrecio().multiply(new BigDecimal(solicitud.cantidad)));
                nueva.setServido(false);
                lineaRepo.saveAndFlush(nueva);
                pedido.getLineasPedido().add(nueva); // Lo añadimos a la lista en memoria
            }

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
            pedidoRepo.saveAndFlush(p);
        }
        mesaRepo.forzarEstadoLibre(idMesa);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{idPedido}/marcar-servido")
    public ResponseEntity<?> marcarPedidoServido(@PathVariable Integer idPedido) {
        Optional<Pedido> pedidoOpt = pedidoRepo.findById(idPedido);
        if (pedidoOpt.isPresent()) {
            Pedido p = pedidoOpt.get();
            for (LineaPedido linea : p.getLineasPedido()) {
                linea.setServido(true);
                lineaRepo.saveAndFlush(linea);
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/linea/{idLinea}/estado-servido")
    public ResponseEntity<?> actualizarEstadoServido(@PathVariable Integer idLinea, @RequestBody Map<String, Boolean> body) {
        Optional<LineaPedido> lineaOpt = lineaRepo.findById(idLinea);
        if (lineaOpt.isPresent()) {
            LineaPedido linea = lineaOpt.get();
            linea.setServido(body.get("servido"));
            lineaRepo.saveAndFlush(linea);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/movil/crear")
    public ResponseEntity<?> crearPedidoDesdeMovil(@RequestBody SolicitudPedidoMovil solicitud) {

        Mesa mesa = mesaRepo.findById(solicitud.idMesa).orElse(null);
        if (mesa == null) return ResponseEntity.badRequest().body("Mesa no encontrada");

        Pedido pedido = pedidoRepo.findByMesa_IdMesaAndEstado(solicitud.idMesa, "ABIERTO")
                .orElseGet(() -> {
                    Pedido p = new Pedido();
                    p.setMesa(mesa);
                    p.setEstado("ABIERTO");
                    p.setFecha(LocalDateTime.now());
                    p.setTotal(BigDecimal.ZERO);
                    p.setLineasPedido(new ArrayList<>());
                    return pedidoRepo.saveAndFlush(p);
                });

        if (!"OCUPADA".equals(mesa.getEstado())) {
            mesa.setEstado("OCUPADA");
            mesaRepo.save(mesa);
        }

        if (solicitud.productos != null) {
            for (SolicitudProducto item : solicitud.productos) {
                Producto prod = productoRepo.findById(item.idProducto).orElse(null);

                if (prod != null) {

                    // 🛠️ LA MISMA MAGIA PARA EL MÓVIL
                    Optional<LineaPedido> lineaExistente = pedido.getLineasPedido().stream()
                            .filter(l -> l.getProducto().getIdProducto().equals(prod.getIdProducto()) && !l.getServido())
                            .findFirst();

                    if (lineaExistente.isPresent()) {
                        LineaPedido linea = lineaExistente.get();
                        int nuevaCant = linea.getCantidad() + item.cantidad;
                        linea.setCantidad(nuevaCant);
                        linea.setSubtotal(prod.getPrecio().multiply(new BigDecimal(nuevaCant)));
                        lineaRepo.saveAndFlush(linea);
                    } else {
                        LineaPedido nueva = new LineaPedido();
                        nueva.setPedido(pedido);
                        nueva.setProducto(prod);
                        nueva.setCantidad(item.cantidad);
                        nueva.setPrecioUnidad(prod.getPrecio());
                        nueva.setSubtotal(prod.getPrecio().multiply(new BigDecimal(item.cantidad)));
                        nueva.setServido(false);
                        lineaRepo.saveAndFlush(nueva);
                        pedido.getLineasPedido().add(nueva); // Lo guardamos en la memoria para que no se pise si piden 2 cosas seguidas
                    }
                }
            }
        }

        BigDecimal total = lineaRepo.calcularTotalPedido(pedido.getIdPedido());
        pedido.setTotal(total);
        pedidoRepo.saveAndFlush(pedido);

        return ResponseEntity.ok(Map.of("mensaje", "Pedido recibido correctamente", "idPedido", pedido.getIdPedido()));
    }

    public static class SolicitudProducto {
        public Integer idProducto;
        public Integer cantidad;
    }

    public static class SolicitudPedidoMovil {
        public Integer idMesa;
        public List<SolicitudProducto> productos;
    }
}