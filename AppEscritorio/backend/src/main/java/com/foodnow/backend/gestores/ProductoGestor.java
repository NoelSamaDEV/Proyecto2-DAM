package com.foodnow.backend.gestores;

import com.foodnow.backend.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoGestor extends JpaRepository<Producto, Integer> {
}