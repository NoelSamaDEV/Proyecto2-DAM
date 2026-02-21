package com.foodnow.backend.controladores;

import com.foodnow.backend.entidades.Mesa;
import com.foodnow.backend.entidades.Pedido;
import com.foodnow.backend.interfaces.MesaInterfaz;
import com.foodnow.backend.repositorios.PedidoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "*")
public class MesaControlador {

    @Autowired
    private MesaInterfaz mesaRepo;

    // Repositorio de pedidos para poder cerrarlos
    @Autowired
    private PedidoRepositorio pedidoRepo;

    @GetMapping
    public List<Mesa> obtenerTodas() {
        return mesaRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mesa> obtenerPorId(@PathVariable Integer id) {
        // CORREGIDO: Declaración limpia de la variable
        Optional<Mesa> mesaOpt = mesaRepo.findById(id);
        return mesaOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/atender")
    public ResponseEntity<?> atenderMesa(@PathVariable Integer id) {
        mesaRepo.forzarEstadoOcupada(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/liberar")
    public ResponseEntity<?> liberarMesa(@PathVariable Integer id) {
        // 1. Ponemos la mesa como LIBRE
        mesaRepo.forzarEstadoLibre(id);

        // 2. Buscamos si la mesa tenía un pedido ABIERTO
        Optional<Pedido> pedidoOpt = pedidoRepo.findByMesa_IdMesaAndEstado(id, "ABIERTO");

        // 3. Si lo tiene, lo pasamos a CERRADO para que no se mezclen con el siguiente cliente
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            pedido.setEstado("CERRADO");
            pedidoRepo.saveAndFlush(pedido);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/ayuda")
    public ResponseEntity<?> pedirAyuda(@PathVariable Integer id) {
        Optional<Mesa> mesaOpt = mesaRepo.findById(id);
        if (mesaOpt.isPresent()) {
            Mesa mesa = mesaOpt.get();
            mesa.setEstado("AYUDA");
            mesaRepo.save(mesa);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/cuenta")
    public ResponseEntity<?> pedirCuenta(@PathVariable Integer id) {
        Optional<Mesa> mesaOpt = mesaRepo.findById(id);
        if (mesaOpt.isPresent()) {
            Mesa mesa = mesaOpt.get();
            mesa.setEstado("PIDIENDO_CUENTA");
            mesaRepo.save(mesa);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}