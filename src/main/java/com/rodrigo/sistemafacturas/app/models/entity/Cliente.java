package com.rodrigo.sistemafacturas.app.models.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id_cliente;

    private String nombre;
    private String apellido;
    private String email;
}
