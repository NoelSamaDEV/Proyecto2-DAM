package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.Categoria;
import com.foodnow.backend.interfaces.CategoriaInterfaz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaGestor {

    @Autowired
    private CategoriaInterfaz categoriaInterfaz;

    public List<Categoria> obtenerTodas() {
        return categoriaInterfaz.findAll();
    }

    public Optional<Categoria> obtenerPorId(Integer id) {
        return categoriaInterfaz.findById(id);
    }

    public Categoria guardarCategoria(Categoria categoria) {
        return categoriaInterfaz.save(categoria);
    }

    public void borrarCategoria(Integer id) {
        categoriaInterfaz.deleteById(id);
    }
}