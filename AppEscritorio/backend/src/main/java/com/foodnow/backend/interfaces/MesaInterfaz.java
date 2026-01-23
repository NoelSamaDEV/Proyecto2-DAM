package com.foodnow.backend.interfaces;

import com.foodnow.backend.entidades.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MesaInterfaz extends JpaRepository<Mesa, Integer> {

    // ESTO FUERZA EL CAMBIO DE ESTADO EN LA BASE DE DATOS DIRECTAMENTE
    @Modifying
    @Transactional
    @Query(value = "UPDATE mesa SET estado = 'OCUPADA' WHERE id_mesa = :idMesa", nativeQuery = true)
    void forzarEstadoOcupada(@Param("idMesa") Integer idMesa);

    @Modifying
    @Transactional
    @Query(value = "UPDATE mesa SET estado = 'LIBRE' WHERE id_mesa = :idMesa", nativeQuery = true)
    void forzarEstadoLibre(@Param("idMesa") Integer idMesa);
}