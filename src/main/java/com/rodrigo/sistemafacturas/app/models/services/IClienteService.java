package com.rodrigo.sistemafacturas.app.models.services;

import com.rodrigo.sistemafacturas.app.models.entity.Cliente;
import com.rodrigo.sistemafacturas.app.models.entity.Factura;
import com.rodrigo.sistemafacturas.app.models.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IClienteService {

    List<Cliente> findAll();

    Page<Cliente> findAll(Pageable pageable);

    void save(Cliente cliente);

    Cliente findOne(Long id);

    Cliente fetchByIdWithFacturas(Long id);

    void delete(Long id);

    List<Producto> findByNombre(String term);

    void saveFactura(Factura factura);

    Producto findProductoById(Long id);

    Factura findFacturaById(Long id);

    void deleteFactura(Long id);

    Factura fetchFacturaByIdWithClienteWithItemFacturaWithProducto(Long id);
}
