package com.rodrigo.sistemafacturas.app.models.dao;

import com.rodrigo.sistemafacturas.app.models.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IProductoDao extends JpaRepository<Producto, Long> {

    @Query("select p from Producto p where p.nombre like %?1%")
    List<Producto> findByNombre(String term);
}
