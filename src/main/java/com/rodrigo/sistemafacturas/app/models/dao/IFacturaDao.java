package com.rodrigo.sistemafacturas.app.models.dao;

import com.rodrigo.sistemafacturas.app.models.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFacturaDao extends JpaRepository<Factura, Long> {
}
