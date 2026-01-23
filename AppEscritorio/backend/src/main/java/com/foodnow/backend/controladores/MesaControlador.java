package com.foodnow.backend.controladores;

import com.foodnow.backend.entidades.Mesa;
import com.foodnow.backend.entidades.Pedido;
import com.foodnow.backend.gestores.MesaGestor;
import com.foodnow.backend.gestores.PedidoGestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "http://localhost:5173")
public class MesaControlador {

    @Autowired
    private MesaGestor mesaGestor;

    @Autowired
    private PedidoGestor pedidoGestor;

    // GET, POST, DELETE normales...
    @GetMapping
    public List<Mesa> listarMesas() { return mesaGestor.obtenerTodas(); }

    @GetMapping("/{id}")
    public ResponseEntity<Mesa> obtenerMesaPorId(@PathVariable Integer id) {
        return mesaGestor.obtenerPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mesa guardarMesa(@RequestBody Mesa mesa) {
        if (mesa.getEstado() != null) mesa.setEstado(mesa.getEstado().toUpperCase());
        return mesaGestor.guardarMesa(mesa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarMesa(@PathVariable Integer id) {
        if (mesaGestor.obtenerPorId(id).isPresent()) {
            mesaGestor.borrarMesa(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- AQUÍ ESTÁ EL CAMBIO ---
    @PostMapping("/{id}/liberar")
    public ResponseEntity<String> liberarMesa(@PathVariable Integer id) {
        // 1. Buscar Mesa
        Optional<Mesa> posibleMesa = mesaGestor.obtenerPorId(id);
        if (posibleMesa.isEmpty()) return ResponseEntity.notFound().build();

        Mesa mesa = posibleMesa.get();

        // 2. Buscar Pedido Abierto y ELIMINARLO
        Optional<Pedido> pedidoAbierto = pedidoGestor.obtenerPedidoAbierto(id);
        if (pedidoAbierto.isPresent()) {
            // Borramos el pedido de la base de datos
            pedidoGestor.eliminarPedido(pedidoAbierto.get().getIdPedido());
        }

        // 3. Poner Mesa como LIBRE
        mesa.setEstado("LIBRE");
        mesaGestor.guardarMesa(mesa);

        return ResponseEntity.ok("Mesa reseteada y pedido eliminado");
    }
    // 1. SIMULAR: Cliente pide la cuenta
    @PostMapping("/{id}/pedir-cuenta")
    public ResponseEntity<Mesa> pedirCuenta(@PathVariable Integer id) {
        Optional<Mesa> m = mesaGestor.obtenerPorId(id);
        if (m.isPresent()) {
            Mesa mesa = m.get();
            mesa.setEstado("PIDIENDO_CUENTA");
            return ResponseEntity.ok(mesaGestor.guardarMesa(mesa));
        }
        return ResponseEntity.notFound().build();
    }

    // 2. SIMULAR: Cliente llama al camarero
    @PostMapping("/{id}/llamar-camarero")
    public ResponseEntity<Mesa> llamarCamarero(@PathVariable Integer id) {
        Optional<Mesa> m = mesaGestor.obtenerPorId(id);
        if (m.isPresent()) {
            Mesa mesa = m.get();
            mesa.setEstado("AYUDA");
            return ResponseEntity.ok(mesaGestor.guardarMesa(mesa));
        }
        return ResponseEntity.notFound().build();
    }

    // 3. RESOLVER: Camarero atiende (Checkbox)
    @PostMapping("/{id}/atender")
    public ResponseEntity<Mesa> atenderNotificacion(@PathVariable Integer id) {
        Optional<Mesa> m = mesaGestor.obtenerPorId(id);
        if (m.isPresent()) {
            Mesa mesa = m.get();
            // Si está pidiendo ayuda o cuenta, lo devolvemos a estado normal (OCUPADA)
            if (mesa.getEstado().equals("AYUDA") || mesa.getEstado().equals("PIDIENDO_CUENTA")) {
                mesa.setEstado("OCUPADA");
                return ResponseEntity.ok(mesaGestor.guardarMesa(mesa));
            }
        }
        return ResponseEntity.ok().build();
    }
}