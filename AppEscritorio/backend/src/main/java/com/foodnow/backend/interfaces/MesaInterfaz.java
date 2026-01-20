package com.foodnow.backend.interfaces;

import com.foodnow.backend.entidades.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaInterfaz extends JpaRepository<Mesa, Integer> {
}