package com.ERP_Varejo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import com.ERP_Varejo.service.ProdutoService;
import com.ERP_Varejo.service.VendaService;
import com.ERP_Varejo.model.Produto;
import com.ERP_Varejo.model.Venda;
import com.ERP_Varejo.model.ItemVenda;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ErpApplication {

    public static void main(String[] args) {
        // Inicia o framework Spring Boot
        SpringApplication.run(ErpApplication.class, args);
    }

    /* * ========================================================================
     * MANUAL DE INSTRUÇÕES / HISTÓRICO DE VALIDAÇÃO (COMENTADO)
     * ========================================================================
     * Este bloco foi utilizado para validar as funções core do sistema:
     * - Conexão com o banco erp_casadosfogoes
     * - Geração de UUID automático
     * - Baixa de estoque com @Transactional
     * * Para rodar este teste novamente, basta remover os símbolos de comentário 
     * no início e no fim do método abaixo.
     */
    
    /*
    @Bean
    public CommandLineRunner validationTest(ProdutoService produtoService, VendaService vendaService) {
        return (args) -> {
            System.out.println("\n=== INICIANDO VALIDAÇÃO DAS FUNÇÕES ===");

            // 1. TESTE DE PRODUTO E UUID
            Produto feijao = new Produto();
            feijao.setNome("Feijão Carioca 1kg");
            feijao.setQuantidadeEstoque(100);
            feijao.setPrecoVenda(new BigDecimal("8.50"));
            
            feijao = produtoService.salvar(feijao);
            System.out.println("[OK] Produto salvo com UUID: " + feijao.getCodigoSerial());

            // 2. TESTE DE VENDA E BAIXA DE ESTOQUE
            Venda novaVenda = new Venda();
            novaVenda.setMetodoPagamento(Venda.MetodoPagamento.PIX);

            ItemVenda item = new ItemVenda();
            item.setProduto(feijao);
            item.setQuantidade(10); 
            
            List<ItemVenda> itens = new ArrayList<>();
            itens.add(item);
            novaVenda.setItens(itens);

            // Realizando a operação
            vendaService.realizarVenda(novaVenda);
            System.out.println("[OK] Venda realizada com sucesso via PIX!");

            // 3. CONFERÊNCIA FINAL
            Produto produtoAposVenda = produtoService.buscarPorSerial(feijao.getCodigoSerial());
            System.out.println("Estoque inicial: 100 | Estoque atual: " + produtoAposVenda.getQuantidadeEstoque());
            
            if (produtoAposVenda.getQuantidadeEstoque() == 90) {
                System.out.println("=== SISTEMA VALIDADO COM SUCESSO! ===");
            } else {
                System.out.println("!!! ERRO NA VALIDAÇÃO DO ESTOQUE !!!");
            }
        };
    }
    */
}