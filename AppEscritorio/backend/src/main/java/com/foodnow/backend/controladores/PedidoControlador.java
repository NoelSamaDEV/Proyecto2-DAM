package com.foodnow.backend.controladores;

import com.foodnow.backend.modelos.*;
import com.foodnow.backend.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*") // Permite que Vue se conecte
public class PedidoControlador {

    @Autowired private PedidoRepositorio pedidoRepo;
    @Autowired private MesaRepositorio mesaRepo;
    @Autowired private ProductoRepositorio productoRepo;
    @Autowired private LineaPedidoRepositorio lineaRepo;

    // 1. VER EL PEDIDO ACTUAL
    @GetMapping("/mesa/{idMesa}/actual")
    public ResponseEntity<Pedido> obtenerPedidoActual(@PathVariable Integer idMesa) {
        Optional<Pedido> pedido = pedidoRepo.findByMesa_IdMesaAndEstado(idMesa, "ABIERTO");
        if (pedido.isPresent()) {
            return ResponseEntity.ok(pedido.get());
        }
        return ResponseEntity.notFound().build();
    }

    // 2. AÑADIR PRODUCTO (SUMAR A LO QUE YA HAY)
    @PostMapping("/mesa/{idMesa}/agregar")
    public ResponseEntity<Pedido> agregarProducto(
            @PathVariable Integer idMesa,
            @RequestBody SolicitudProducto solicitud) {

        // A. Buscamos la mesa
        Optional<Mesa> m = mesaRepo.findById(idMesa);
        if (!m.isPresent()) return ResponseEntity.notFound().build();
        Mesa mesa = m.get();

        // B. Buscamos si ya hay un pedido ABIERTO. Si no, creamos uno nuevo.
        Pedido pedido = pedidoRepo.findByMesa_IdMesaAndEstado(idMesa, "ABIERTO")
                .orElseGet(() -> {
                    Pedido p = new Pedido();
                    p.setMesa(mesa);
                    p.setEstado("ABIERTO");
                    p.setTotal(BigDecimal.ZERO);

                    // Si creamos pedido, nos aseguramos de que la mesa esté OCUPADA
                    mesa.setEstado("OCUPADA");
                    mesaRepo.save(mesa);

                    return pedidoRepo.save(p);
                });

        // C. Buscamos el producto que quieren añadir (Hamburguesa, Coca-Cola...)
        Optional<Producto> prod = productoRepo.findById(solicitud.idProducto);
        if (prod.isPresent()) {
            Producto producto = prod.get();

            // D. Creamos la línea de pedido nueva
            LineaPedido linea = new LineaPedido();
            linea.setPedido(pedido);
            linea.setProducto(producto);
            linea.setCantidad(solicitud.cantidad);
            linea.setPrecioUnidad(producto.getPrecio());

            // Calculamos: precio x cantidad
            BigDecimal subtotal = producto.getPrecio().multiply(new BigDecimal(solicitud.cantidad));
            linea.setSubtotal(subtotal);

            // Guardamos la línea
            lineaRepo.save(linea);

            // E. SUMAMOS AL TOTAL DEL PEDIDO
            BigDecimal totalActualizado = pedido.getTotal().add(subtotal);
            pedido.setTotal(totalActualizado);

            return ResponseEntity.ok(pedidoRepo.save(pedido));
        }

        return ResponseEntity.badRequest().build();
    }

    // CLASE INTERNA PARA RECIBIR LOS DATOS DEL JSON
    public static class SolicitudProducto {
        public Integer idProducto;
        public Integer cantidad;
    }
}