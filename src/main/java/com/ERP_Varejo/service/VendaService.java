package com.ERP_Varejo.service;

import com.ERP_Varejo.model.Venda;
import com.ERP_Varejo.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class VendaService {

    @Autowired
    private VendaRepository repository;

    public void realizarVenda(Venda venda) {
        repository.save(venda);
    }

    public List<Venda> buscarVendasParaRelatorio(LocalDateTime inicio, LocalDateTime fim) {
        long diasAtras = ChronoUnit.DAYS.between(inicio, LocalDateTime.now());
        
        if (diasAtras > 3) {
            throw new RuntimeException("Acesso negado: Busca limitada aos últimos 3 dias.");
        }

        return repository.findByDataVendaBetween(inicio, fim);
    }
}