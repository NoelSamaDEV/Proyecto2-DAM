package com.foodnow.backend.interfaces;

import com.foodnow.backend.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoInterfaz extends JpaRepository<Producto, Integer> {
}