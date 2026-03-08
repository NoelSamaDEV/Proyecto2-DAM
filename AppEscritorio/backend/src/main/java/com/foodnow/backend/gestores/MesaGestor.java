package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.Mesa;
import com.foodnow.backend.interfaces.MesaInterfaz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MesaGestor {

    @Autowired
    private MesaInterfaz mesaInterfaz;

    public List<Mesa> listarTodo() {
        return mesaInterfaz.findAll();
    }

    // Cambiado de Long a Integer para que coincida con tu entidad
    public Optional<Mesa> buscarPorId(Integer id) {
        return mesaInterfaz.findById(id);
    }

    // Este método es el que pedía PedidoGestor
    public Optional<Mesa> obtenerPorId(Integer id) {
        return mesaInterfaz.findById(id);
    }

    public Mesa guardar(Mesa mesa) {
        // Lógica de autocalcular número si es nueva
        if (mesa.getIdMesa() == null) {
            List<Mesa> todas = mesaInterfaz.findAll();
            int maxNumero = todas.stream()
                    .mapToInt(Mesa::getNumeroMesa)
                    .max()
                    .orElse(0);

            mesa.setNumeroMesa(maxNumero + 1);
            mesa.setEstado("LIBRE");
            mesa.setQrCode("https://proyecto2-dam-production.up.railway.app/mesa/" + (maxNumero + 1));
        }
        return mesaInterfaz.save(mesa);
    }

    // Este método es el que pedía PedidoGestor
    public Mesa guardarMesa(Mesa mesa) {
        return mesaInterfaz.save(mesa);
    }

    // Cambiado de Long a Integer
    public void eliminar(Integer id) {
        mesaInterfaz.deleteById(id);
    }
}