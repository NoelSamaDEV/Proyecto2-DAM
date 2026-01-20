package com.foodnow.backend.controladores;

import com.foodnow.backend.entidades.Categoria;
import com.foodnow.backend.gestores.CategoriaGestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaControlador {

    @Autowired
    private CategoriaGestor categoriaGestor;

    @GetMapping
    public List<Categoria> obtenerTodas() {
        return categoriaGestor.obtenerTodas();
    }

    @GetMapping("/{id}")
    public Optional<Categoria> obtenerPorId(@PathVariable Integer id) {
        return categoriaGestor.obtenerPorId(id);
    }

    @PostMapping
    public Categoria guardar(@RequestBody Categoria categoria) {
        return categoriaGestor.guardarCategoria(categoria);
    }

    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Integer id) {
        categoriaGestor.borrarCategoria(id);
    }
}