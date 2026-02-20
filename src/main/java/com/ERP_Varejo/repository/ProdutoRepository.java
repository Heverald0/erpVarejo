package com.ERP_Varejo.repository;

import com.ERP_Varejo.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    // Método de busca feito a partir do serial 
    Produto findByCodigoSerial(String codigoSerial);

    // Método de busca feito a partir do nome do produto
    List<Produto> findByNomeContainingIgnoreCase(String nome);
}