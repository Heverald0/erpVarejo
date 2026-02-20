package com.ERP_Varejo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    
    @Column(unique = true)
    private String login;
    
    private String senha;

    @Enumerated(EnumType.STRING)
    private Perfil cargo;

    public enum Perfil {
        ADMIN, OPERADOR
    }
}