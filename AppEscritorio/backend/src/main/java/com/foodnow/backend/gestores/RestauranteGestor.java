package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestauranteGestor extends JpaRepository<Restaurante, Integer> {
}