package com.foodnow.backend.controladores;

import com.foodnow.backend.entidades.Mesa;
import com.foodnow.backend.gestores.MesaGestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "http://localhost:5173") // Permite Vue
public class MesaControlador {

    @Autowired
    private MesaGestor mesaGestor;

    @GetMapping
    public List<Mesa> obtenerMesas() {
        return mesaGestor.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mesa> obtenerMesa(@PathVariable Integer id) {
        return mesaGestor.obtenerPorId(id)
                .map(mesa -> ResponseEntity.ok(mesa))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mesa crearMesaAutomatica() {
        return mesaGestor.crearMesaAutomatica();
    }

    // ESTE ES EL MÉTODO QUE NECESITAS PARA EL BOTÓN DE BORRAR
    @DeleteMapping("/{id}")
    public void borrarMesa(@PathVariable Integer id) {
        mesaGestor.borrarMesa(id);
    }
}