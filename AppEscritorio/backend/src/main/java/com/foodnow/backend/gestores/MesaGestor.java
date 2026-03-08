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
    private MesaInterfaz mesaInterfaz; // Usamos tu nombre real de la interfaz

    public List<Mesa> listarTodo() {
        return mesaInterfaz.findAll();
    }

    public Optional<Mesa> buscarPorId(Long id) {
        return mesaInterfaz.findById(id);
    }

    public Mesa guardar(Mesa mesa) {
        // Si el id es null, significa que es una mesa nueva pulsada desde el botón
        if (mesa.getIdMesa() == null) {
            List<Mesa> todas = mesaInterfaz.findAll();

            // Buscamos el número de mesa más alto actual
            int maxNumero = todas.stream()
                    .mapToInt(Mesa::getNumeroMesa)
                    .max()
                    .orElse(0);

            // Asignamos el siguiente número automáticamente
            mesa.setNumeroMesa(maxNumero + 1);
            mesa.setEstado("LIBRE");
            // Generamos el QR con el nuevo número
            mesa.setQrCode("https://proyecto2-dam-production.up.railway.app/mesa/" + (maxNumero + 1));
        }
        return mesaInterfaz.save(mesa);
    }

    public void eliminar(Long id) {
        mesaInterfaz.deleteById(id);
    }
}