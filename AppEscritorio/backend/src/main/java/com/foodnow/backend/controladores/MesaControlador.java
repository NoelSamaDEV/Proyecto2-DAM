package com.foodnow.backend.controladores;

import com.foodnow.backend.entidades.Mesa;
import com.foodnow.backend.gestores.MesaGestor;
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

    // 1. GET (TODAS): Listar todas las mesas
    @GetMapping
    public List<Mesa> listarMesas() {
        return mesaGestor.obtenerTodas();
    }

    // --- ¡ESTA ES LA FUNCIÓN NUEVA QUE FALTABA! ---
    // 2. GET (UNA): Obtener una mesa específica por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Mesa> obtenerMesaPorId(@PathVariable Integer id) {
        Optional<Mesa> posibleMesa = mesaGestor.obtenerPorId(id);

        // Si la encuentra, la devuelve. Si no, devuelve error 404.
        return posibleMesa.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // ---------------------------------------------

    // 3. POST: Guardar mesa
    @PostMapping
    public Mesa guardarMesa(@RequestBody Mesa mesa) {
        // Nos aseguramos de que el estado sea mayúsculas por coherencia
        if (mesa.getEstado() != null) {
            mesa.setEstado(mesa.getEstado().toUpperCase());
        }
        return mesaGestor.guardarMesa(mesa);
    }

    // 4. DELETE: Borrar mesa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarMesa(@PathVariable Integer id) {
        Optional<Mesa> posibleMesa = mesaGestor.obtenerPorId(id);

        if (posibleMesa.isPresent()) {
            mesaGestor.borrarMesa(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}