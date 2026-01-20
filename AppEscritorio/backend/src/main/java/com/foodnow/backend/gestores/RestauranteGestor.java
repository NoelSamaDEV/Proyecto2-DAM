package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.Restaurante;
import com.foodnow.backend.interfaces.RestauranteInterfaz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestauranteGestor {

    @Autowired
    private RestauranteInterfaz restauranteInterfaz;

    // 1. Obtener todos
    public List<Restaurante> obtenerTodos() {
        return restauranteInterfaz.findAll();
    }

    // 2. Obtener por ID
    public Optional<Restaurante> obtenerPorId(Integer id) {
        return restauranteInterfaz.findById(id);
    }

    // 3. Guardar
    public Restaurante guardarRestaurante(Restaurante restaurante) {
        return restauranteInterfaz.save(restaurante);
    }

    // 4. Borrar
    public void borrarRestaurante(Integer id) {
        restauranteInterfaz.deleteById(id);
    }
}