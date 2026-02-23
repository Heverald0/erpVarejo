package com.ERP_Varejo.repository;

import com.ERP_Varejo.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    // busca por serial
    Produto findByCodigoSerial(String codigoSerial);

    //  busca por nome
    List<Produto> findByNomeContainingIgnoreCase(String nome); 
}