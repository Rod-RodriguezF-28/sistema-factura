package com.rodrigo.sistemafacturas.app.models.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_cliente_2;

    private String nombre;
    private String apellido;
    private String email;
}
