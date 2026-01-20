package com.foodnow.backend.interfaces;

import com.foodnow.backend.entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaInterfaz extends JpaRepository<Categoria, Integer> {
}