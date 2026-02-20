package com.ERP_Varejo.service;

import com.ERP_Varejo.model.ItemVenda;
import com.ERP_Varejo.model.Produto;
import com.ERP_Varejo.model.Venda;
import com.ERP_Varejo.repository.ProdutoRepository;
import com.ERP_Varejo.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public Venda realizarVenda(Venda venda) {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemVenda item : venda.getItens()) {
            // Buscar atualizada no banco
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: ID " + item.getProduto().getId()));

            // Validação de estoque suficiente
            if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            // Baixa de estoque
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
            produtoRepository.save(produto);

            // Configurar preço histórico e somar total
            item.setPrecoUnitarioHistorico(produto.getPrecoVenda());
            item.setVenda(venda); 

            BigDecimal subtotal = item.getPrecoUnitarioHistorico().multiply(new BigDecimal(item.getQuantidade()));
            total = total.add(subtotal);
        }

        venda.setTotalVenda(total);
        return vendaRepository.save(venda);
    }
}