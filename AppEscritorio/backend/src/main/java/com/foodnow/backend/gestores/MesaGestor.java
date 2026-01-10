package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaGestor extends JpaRepository<Mesa, Integer> {
}