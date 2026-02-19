package com.ERP_Varejo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Data
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String codigoSerial;

    @Column(nullable = false)
    private String nome;

    private Integer quantidadeEstoque;

    private BigDecimal precoCusto;

    @Column(nullable = false)
    private BigDecimal precoVenda;
}