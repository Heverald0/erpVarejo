package com.ERP_Varejo.repository;

import com.ERP_Varejo.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Integer> {

    // Busca específica por periodo
    List<Venda> findByDataVendaBetween(LocalDateTime inicio, LocalDateTime fim);

    // Busca por método de pagamento
    List<Venda> findByMetodoPagamento(Venda.MetodoPagamento metodo);

    @Query("SELECT SUM(v.totalVenda) FROM Venda v WHERE v.dataVenda BETWEEN :inicio AND :fim")
    BigDecimal somarTotalVendasNoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}