package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaGestor extends JpaRepository<Categoria, Integer> {
}