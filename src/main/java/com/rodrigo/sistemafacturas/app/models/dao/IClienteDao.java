package com.rodrigo.sistemafacturas.app.models.dao;

import com.rodrigo.sistemafacturas.app.models.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IClienteDao extends JpaRepository<Cliente, Long> {

}
