package com.rodrigo.sistemafacturas.app.models.dao;

import com.rodrigo.sistemafacturas.app.models.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IClienteDao extends JpaRepository<Cliente, Long> {
    @Query("select c from Cliente c left join fetch c.facturas f where c.id=?1")
    Cliente fetchByIdWithFacturas(Long id);

}
