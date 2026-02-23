package com.ERP_Varejo.repository;

import com.ERP_Varejo.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Integer> {

    List<Venda> findByDataVendaBetween(LocalDateTime inicio, LocalDateTime fim);

}