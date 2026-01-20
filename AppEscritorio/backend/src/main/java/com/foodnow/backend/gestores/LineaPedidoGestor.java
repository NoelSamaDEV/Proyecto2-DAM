package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.LineaPedido;
import com.foodnow.backend.interfaces.LineaPedidoInterfaz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LineaPedidoGestor {

    @Autowired
    private LineaPedidoInterfaz lineaPedidoInterfaz;

    public List<LineaPedido> obtenerTodas() {
        return lineaPedidoInterfaz.findAll();
    }

    public Optional<LineaPedido> obtenerPorId(Integer id) {
        return lineaPedidoInterfaz.findById(id);
    }

    public LineaPedido guardarLinea(LineaPedido linea) {
        return lineaPedidoInterfaz.save(linea);
    }

    public void borrarLinea(Integer id) {
        lineaPedidoInterfaz.deleteById(id);
    }
}