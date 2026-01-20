package com.foodnow.backend.controladores;

import com.foodnow.backend.entidades.Producto;
import com.foodnow.backend.gestores.ProductoGestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
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

    @PostMapping
    public Producto guardar(@RequestBody Producto producto) {
        return productoGestor.guardarProducto(producto);
    }

    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Integer id) {
        productoGestor.borrarProducto(id);
    }
}