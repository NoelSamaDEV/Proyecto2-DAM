package com.foodnow.backend.controladores;

import com.foodnow.backend.entidades.Pedido;
import com.foodnow.backend.gestores.PedidoGestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoControlador {

    @Autowired
    private PedidoGestor pedidoGestor;

    @GetMapping
    public List<Pedido> obtenerTodos() {
        return pedidoGestor.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Optional<Pedido> obtenerPorId(@PathVariable Integer id) {
        return pedidoGestor.obtenerPorId(id);
    }

    @PostMapping
    public Pedido guardar(@RequestBody Pedido pedido) {
        return pedidoGestor.guardarPedido(pedido);
    }

    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Integer id) {
        pedidoGestor.borrarPedido(id);
    }
}