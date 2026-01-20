package com.foodnow.backend.interfaces;

import com.foodnow.backend.entidades.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestauranteInterfaz extends JpaRepository<Restaurante, Integer> {
}