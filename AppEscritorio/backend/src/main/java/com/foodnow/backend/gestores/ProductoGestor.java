package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.Producto;
import com.foodnow.backend.interfaces.ProductoInterfaz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoGestor {

    @Autowired
    private ProductoInterfaz productoInterfaz;

    public List<Producto> obtenerTodos() {
        return productoInterfaz.findAll();
    }

    public Optional<Producto> obtenerPorId(Integer id) {
        return productoInterfaz.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        return productoInterfaz.save(producto);
    }

    public void borrarProducto(Integer id) {
        productoInterfaz.deleteById(id);
    }
}