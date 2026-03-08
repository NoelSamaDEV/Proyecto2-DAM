package com.foodnow.backend.controladores;

import com.foodnow.backend.dto.CuentaResponseDTO;
import com.foodnow.backend.entidades.Mesa;
import com.foodnow.backend.entidades.Pedido;
import com.foodnow.backend.interfaces.MesaInterfaz;
import com.foodnow.backend.repositorios.PedidoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "*")
public class MesaControlador {

    @Autowired
    private MesaInterfaz mesaRepo;

    @Autowired
    private PedidoRepositorio pedidoRepo;

    @GetMapping
    public List<Mesa> obtenerTodas() {
        return mesaRepo.findAll();
    }

    /**
     * NUEVO: Crea una mesa automáticamente calculando el siguiente número.
     * Recibe un body vacío {} desde el frontend.
     */
    @PostMapping
    public Mesa crearMesaAutomatica(@RequestBody Mesa mesaData) {
        List<Mesa> todas = mesaRepo.findAll();

        // Calculamos el número más alto actual
        int maxNumero = todas.stream()
                .mapToInt(Mesa::getNumeroMesa)
                .max()
                .orElse(0);

        Mesa nuevaMesa = new Mesa();
        nuevaMesa.setNumeroMesa(maxNumero + 1);
        nuevaMesa.setEstado("LIBRE");
        // Generamos el QR apuntando a tu backend de Railway
        nuevaMesa.setQrCode("https://proyecto2-dam-production.up.railway.app/mesa/" + (maxNumero + 1));

        return mesaRepo.save(nuevaMesa);
    }

    /**
     * NUEVO: Elimina una mesa por ID.
     * Gracias al ON DELETE CASCADE en la DB, se borrarán sus pedidos automáticamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMesa(@PathVariable Integer id) {
        if (mesaRepo.existsById(id)) {
            mesaRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/ticket")
    public ResponseEntity<?> obtenerTicket(@PathVariable Integer id) {
        Optional<Pedido> pedidoOpt = pedidoRepo.findByMesa_IdMesaAndEstado(id, "ABIERTO");

        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();

            List<CuentaResponseDTO.LineaCuentaDTO> lineasDto = pedido.getLineasPedido().stream()
                    .map(linea -> new CuentaResponseDTO.LineaCuentaDTO(
                            linea.getCantidad(),
                            linea.getProducto().getNombre(),
                            linea.getSubtotal()
                    )).collect(Collectors.toList());

            BigDecimal totalCalculado = pedido.getLineasPedido().stream()
                    .map(l -> l.getSubtotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return ResponseEntity.ok(new CuentaResponseDTO(lineasDto, totalCalculado));
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mesa> obtenerPorId(@PathVariable Integer id) {
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
        mesaRepo.forzarEstadoLibre(id);
        Optional<Pedido> pedidoOpt = pedidoRepo.findByMesa_IdMesaAndEstado(id, "ABIERTO");
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