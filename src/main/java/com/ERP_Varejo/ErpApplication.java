package com.ERP_Varejo;

import com.ERP_Varejo.model.Usuario;
import com.ERP_Varejo.model.Produto;
import com.ERP_Varejo.model.Venda;
import com.ERP_Varejo.model.ItemVenda;
import com.ERP_Varejo.service.UsuarioService;
import com.ERP_Varejo.service.ProdutoService;
import com.ERP_Varejo.service.VendaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ErpApplication {

    public static void main(String[] args) {
        // Inicializa o Spring Boot e o servidor embutido (Tomcat) na porta 8080
        SpringApplication.run(ErpApplication.class, args);
    }

    /**
     * BLOCO ATIVO: Configuração Inicial de Segurança
     * Este método valida se o administrador existe e garante o uso de BCrypt.
     */
    @Bean
    public CommandLineRunner setupAdmin(UsuarioService usuarioService) {
        return (args) -> {
            System.out.println("\n=== [FASE 1] VERIFICANDO SEGURANÇA DO SISTEMA ===");
            
            // Valida se o usuário 'admin' já possui hash BCrypt no banco
            if (usuarioService.autenticar("admin", "admin123") == null) {
                System.out.println("Criando usuário administrador padrão com senha criptografada...");
                
                Usuario admin = new Usuario();
                admin.setNome("Heveraldo Admin");
                admin.setLogin("admin");
                admin.setSenha("admin123"); // O UsuarioService aplicará o BCrypt automaticamente
                
                
                usuarioService.salvar(admin);
                System.out.println("[OK] Admin criado. Verifique o hash no pgAdmin na tabela 'usuarios'.");
            } else {
                System.out.println("[OK] Acesso administrativo validado e ativo.");
            }
            System.out.println("================================================\n");
        };
    }

    /* * ========================================================================
     * BLOCO COMENTADO: MANUAL DE INSTRUÇÕES E TESTES DE INTEGRIDADE
     * ========================================================================
     * Este código abaixo foi o responsável por validar as operações de estoque.
     * Use-o como referência para entender o fluxo de persistência.
     */
    
    /* @Bean
    public CommandLineRunner validationTest(ProdutoService produtoService, VendaService vendaService) {
        return (args) -> {
            System.out.println("\n=== [MANUAL] INICIANDO VALIDAÇÃO DE ESTOQUE E VENDAS ===");

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

            vendaService.realizarVenda(novaVenda);
            System.out.println("[OK] Venda realizada com sucesso via PIX!");

            Produto produtoAposVenda = produtoService.buscarPorSerial(feijao.getCodigoSerial());
            System.out.println("Status: Estoque inicial 100 -> Estoque atual: " + produtoAposVenda.getQuantidadeEstoque());
            
            if (produtoAposVenda.getQuantidadeEstoque() == 90) {
                System.out.println("=== [OK] INTEGRIDADE DO BANCO DE DADOS VALIDADA! ===");
            } else {
                System.out.println("!!! [ALERTA] FALHA NA BAIXA DE ESTOQUE !!!");
            }
            System.out.println("========================================================\n");
        };
    }
    */
} // Chave final que fecha a classe ErpApplication