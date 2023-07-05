package com.rodrigo.sistemafacturas.app.models.dao;

import com.rodrigo.sistemafacturas.app.models.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IFacturaDao extends JpaRepository<Factura, Long> {

    @Query("select f from Factura f join fetch f.cliente c join fetch f.items l join fetch l.producto where f.id=?1")
    Factura fetchByIdWithClienteWithItemFacturaWithProducto(Long id);
}
