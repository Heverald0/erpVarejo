package com.ERP_Varejo;

import com.ERP_Varejo.model.Usuario;
import com.ERP_Varejo.service.UsuarioService;
import com.ERP_Varejo.view.LoginView;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import javax.swing.*;

@SpringBootApplication
public class ErpApplication {

    public static void main(String[] args) {
        // Configuração necessária para rodar interfaces gráficas com Spring Boot
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ErpApplication.class);
        builder.headless(false); // Desativa o modo "sem cabeça" para permitir janelas
        builder.run(args);
    }

    /**
     * BLOCO ATIVO: Configuração de Segurança e Interface
     */
    @Bean
    public CommandLineRunner setupSecurity(UsuarioService usuarioService, LoginView loginView) {
        return (args) -> {
            System.out.println("\n=== [FASE 1] CONFIGURANDO AMBIENTE DE ACESSO ===");
            
            // 1. Criar Admin se não existir
            if (usuarioService.autenticar("admin", "admin123") == null) {
                Usuario admin = new Usuario();
                admin.setNome("Heveraldo Admin");
                admin.setLogin("admin");
                admin.setSenha("admin123");
                admin.setCargo(Usuario.Perfil.ADMIN);
                usuarioService.salvar(admin);
                System.out.println("[OK] Usuário ADMINISTRADOR pronto.");
            }

            // 2. Criar Operador para teste de travas de segurança
            if (usuarioService.autenticar("caixa1", "senha123") == null) {
                Usuario operador = new Usuario();
                operador.setNome("Operador de Caixa");
                operador.setLogin("caixa1");
                operador.setSenha("senha123");
                operador.setCargo(Usuario.Perfil.OPERADOR);
                usuarioService.salvar(operador);
                System.out.println("[OK] Usuário OPERADOR pronto para teste de permissões.");
            }

            // 3. Iniciar Interface Gráfica
            SwingUtilities.invokeLater(() -> loginView.exibir());
            
            System.out.println("================================================\n");
        };
    }
    
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
 // Chave final que fecha a classe ErpApplication