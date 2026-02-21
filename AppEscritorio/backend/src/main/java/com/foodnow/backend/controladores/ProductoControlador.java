package com.foodnow.backend.controladores;

import com.foodnow.backend.entidades.Producto;
import com.foodnow.backend.interfaces.ProductoInterfaz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoControlador {

    @Autowired
    private ProductoInterfaz productoRepo;

    // Obtener todos los productos
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoRepo.findAll();
    }

    // Obtener un producto por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Integer id) {
        Optional<Producto> productoOpt = productoRepo.findById(id);
        return productoOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener productos filtrados por el ID de la categoría
    @GetMapping("/categoria/{idCategoria}")
    public List<Producto> obtenerPorCategoria(@PathVariable Integer idCategoria) {
        return productoRepo.findAll().stream()
                .filter(p -> p.getCategoria() != null && p.getCategoria().getIdCategoria().equals(idCategoria))
                .collect(Collectors.toList());
    }

    // Crear un nuevo producto
    @PostMapping
    public Producto crear(@RequestBody Producto producto) {
        return productoRepo.save(producto);
    }

    // Actualizar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Integer id, @RequestBody Producto productoDetalles) {
        return productoRepo.findById(id).map(producto -> {
            producto.setNombre(productoDetalles.getNombre());
            producto.setPrecio(productoDetalles.getPrecio());
            producto.setImagen(productoDetalles.getImagen());
            producto.setDescripcion(productoDetalles.getDescripcion());
            producto.setCategoria(productoDetalles.getCategoria());
            return ResponseEntity.ok(productoRepo.save(producto));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (productoRepo.existsById(id)) {
            productoRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}