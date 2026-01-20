package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.Mesa;
import com.foodnow.backend.entidades.Restaurante;
import com.foodnow.backend.interfaces.MesaInterfaz;
import com.foodnow.backend.interfaces.RestauranteInterfaz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MesaGestor {

    @Autowired
    private MesaInterfaz mesaInterfaz;

    @Autowired
    private RestauranteInterfaz restauranteInterfaz;

    public List<Mesa> obtenerTodas() {
        return mesaInterfaz.findAll();
    }

    public Optional<Mesa> obtenerPorId(Integer id) {
        return mesaInterfaz.findById(id);
    }

    public Mesa guardarMesa(Mesa mesa) {
        return mesaInterfaz.save(mesa);
    }

    // MÉTODO DE BORRADO
    public void borrarMesa(Integer id) {
        mesaInterfaz.deleteById(id);
    }

    public Mesa crearMesaAutomatica() {
        long totalMesas = mesaInterfaz.count();
        int siguienteNumero = (int) totalMesas + 1;
        String codigoUnico = UUID.randomUUID().toString().substring(0, 8);
        String qrFinal = "FOODNOW-MESA-" + siguienteNumero + "-" + codigoUnico;

        Mesa nuevaMesa = new Mesa();
        nuevaMesa.setNumeroMesa(siguienteNumero);
        nuevaMesa.setQrCode(qrFinal);
        nuevaMesa.setEstado("LIBRE");

        Restaurante restaurante = restauranteInterfaz.findById(1).orElse(null);
        nuevaMesa.setRestaurante(restaurante);

        return mesaInterfaz.save(nuevaMesa);
    }
}