package com.foodnow.backend.controladores;

import com.foodnow.backend.entidades.Restaurante;
import com.foodnow.backend.gestores.RestauranteGestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteControlador {

    @Autowired
    private RestauranteGestor restauranteGestor; // Llamamos al jefe (Gestor)

    // 1. GET: Da todos los restaurantes
    // URL: http://localhost:8080/api/restaurantes
    @GetMapping
    public List<Restaurante> obtenerTodos() {
        return restauranteGestor.obtenerTodos();
    }

    // 2. GET: Da un restaurante por ID
    // URL: http://localhost:8080/api/restaurantes/1
    @GetMapping("/{id}")
    public Optional<Restaurante> obtenerPorId(@PathVariable Integer id) {
        return restauranteGestor.obtenerPorId(id);
    }

    // 3. POST: Crea un restaurante nuevo
    // URL: http://localhost:8080/api/restaurantes
    @PostMapping
    public Restaurante guardar(@RequestBody Restaurante restaurante) {
        return restauranteGestor.guardarRestaurante(restaurante);
    }

    // 4. DELETE: Borra un restaurante
    // URL: http://localhost:8080/api/restaurantes/1
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Integer id) {
        restauranteGestor.borrarRestaurante(id);
    }
}