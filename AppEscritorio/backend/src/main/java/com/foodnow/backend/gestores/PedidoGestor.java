package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.Pedido;
import com.foodnow.backend.interfaces.PedidoInterfaz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoGestor {

    @Autowired
    private PedidoInterfaz pedidoInterfaz;

    public List<Pedido> obtenerTodos() {
        return pedidoInterfaz.findAll();
    }

    public Optional<Pedido> obtenerPorId(Integer id) {
        return pedidoInterfaz.findById(id);
    }

    public Pedido guardarPedido(Pedido pedido) {
        return pedidoInterfaz.save(pedido);
    }

    public void borrarPedido(Integer id) {
        pedidoInterfaz.deleteById(id);
    }
}