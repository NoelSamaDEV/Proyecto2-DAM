package com.foodnow.backend.controladores;

import com.foodnow.backend.entidades.Producto;
import com.foodnow.backend.gestores.ProductoGestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // <--- IMPORTANTE: Permite acceso desde el móvil
public class ProductoControlador {

    @Autowired
    private ProductoGestor productoGestor;

    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoGestor.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Optional<Producto> obtenerPorId(@PathVariable Integer id) {
        return productoGestor.obtenerPorId(id);
    }

    // --- NUEVO ENDPOINT PARA EL MÓVIL (Filtrar por categoría) ---
    @GetMapping("/categoria/{id}")
    public ResponseEntity<List<Producto>> obtenerProductosPorCategoria(@PathVariable Integer id) {

        List<Producto> todos = productoGestor.obtenerTodos();
        List<Producto> filtrados = new ArrayList<>();

        for (Producto p : todos) {
            // Comprueba si el producto tiene categoría, y si el ID coincide
            if (p.getCategoria() != null && p.getCategoria().getIdCategoria().equals(id)) {
                filtrados.add(p);
            }
        }

        return ResponseEntity.ok(filtrados);
    }

    @PostMapping
    public Producto guardar(@RequestBody Producto producto) {
        return productoGestor.guardarProducto(producto);
    }

    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Integer id) {
        productoGestor.borrarProducto(id);
    }
}