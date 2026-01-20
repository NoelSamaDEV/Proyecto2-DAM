package com.foodnow.backend.controladores;

import com.foodnow.backend.entidades.LineaPedido;
import com.foodnow.backend.gestores.LineaPedidoGestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lineas")
public class LineaPedidoControlador {

    @Autowired
    private LineaPedidoGestor lineaPedidoGestor;

    @GetMapping
    public List<LineaPedido> obtenerTodas() {
        return lineaPedidoGestor.obtenerTodas();
    }

    @GetMapping("/{id}")
    public Optional<LineaPedido> obtenerPorId(@PathVariable Integer id) {
        return lineaPedidoGestor.obtenerPorId(id);
    }

    @PostMapping
    public LineaPedido guardar(@RequestBody LineaPedido linea) {
        return lineaPedidoGestor.guardarLinea(linea);
    }

    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Integer id) {
        lineaPedidoGestor.borrarLinea(id);
    }
}