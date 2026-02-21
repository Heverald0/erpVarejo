package com.ERP_Varejo.view;

import com.ERP_Varejo.model.Produto;
import com.ERP_Varejo.model.Usuario;
import com.ERP_Varejo.model.Venda;
import com.ERP_Varejo.model.ItemVenda;
import com.ERP_Varejo.service.ProdutoService;
import com.ERP_Varejo.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class VendaView extends JFrame {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private VendaService vendaService;

    private JTextField txtCodProduto, txtQtd;
    private JTable tabelaItens;
    private DefaultTableModel modelTabela;
    private JLabel lblTotal;
    private BigDecimal totalGeral = BigDecimal.ZERO;
    private Usuario operador;
    private List<ItemVenda> listaItensVenda = new ArrayList<>();

    public void exibir(Usuario usuario) {
        this.operador = usuario;
        setTitle("PDV - CasadosFogões | Operador: " + usuario.getNome());
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PAINEL TOPO: IDENTIFICAÇÃO E ENTRADA ---
        JPanel pnlTopo = new JPanel(new BorderLayout());
        pnlTopo.setBackground(new Color(44, 62, 80));
        
        JPanel pnlInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        pnlInput.setOpaque(false);

        txtCodProduto = new JTextField(15);
        txtCodProduto.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtQtd = new JTextField("1", 5);
        txtQtd.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel l1 = new JLabel("CÓDIGO (SERIAL):"); l1.setForeground(Color.WHITE);
        JLabel l2 = new JLabel("QTD:"); l2.setForeground(Color.WHITE);

        pnlInput.add(l1); pnlInput.add(txtCodProduto);
        pnlInput.add(l2); pnlInput.add(txtQtd);

        JLabel lblAtalhos = new JLabel(" [F2] PESQUISAR  |  [F5] FINALIZAR  |  [ESC] SAIR    ");
        lblAtalhos.setForeground(new Color(236, 240, 241));
        lblAtalhos.setFont(new Font("Segoe UI", Font.BOLD, 12));

        pnlTopo.add(pnlInput, BorderLayout.CENTER);
        pnlTopo.add(lblAtalhos, BorderLayout.EAST);

        // --- TABELA DE ITENS ---
        String[] colunas = {"Item", "Descrição", "Qtd", "V. Unit", "Subtotal"};
        modelTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaItens = new JTable(modelTabela);
        tabelaItens.setRowHeight(30);
        tabelaItens.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

        // --- PAINEL INFERIOR ---
        JPanel pnlInferior = new JPanel(new BorderLayout());
        pnlInferior.setBackground(Color.WHITE);
        pnlInferior.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        lblTotal = new JLabel("TOTAL: R$ 0,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 60));
        lblTotal.setForeground(new Color(39, 174, 96));
        
        pnlInferior.add(lblTotal, BorderLayout.EAST);

        add(pnlTopo, BorderLayout.NORTH);
        add(pnlInferior, BorderLayout.SOUTH);

        configurarEventos();
        setVisible(true);
        txtCodProduto.requestFocus();
    }

    private void configurarEventos() {
        KeyAdapter adapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> adicionarItem();
                    case KeyEvent.VK_F2 -> abrirModalPesquisa();
                    case KeyEvent.VK_F5 -> finalizarVenda();
                    case KeyEvent.VK_ESCAPE -> dispose();
                }
            }
        };
        txtCodProduto.addKeyListener(adapter);
        txtQtd.addKeyListener(adapter);
    }

    private void adicionarItem() {
        try {
            String codigo = txtCodProduto.getText().trim();
            Produto p = produtoService.buscarPorSerial(codigo);

            if (p != null) {
                int qtd = Integer.parseInt(txtQtd.getText());
                
                if (p.getQuantidadeEstoque() < qtd) {
                    JOptionPane.showMessageDialog(this, "Estoque insuficiente! Disponível: " + p.getQuantidadeEstoque());
                    return;
                }

                BigDecimal subtotal = p.getPrecoVenda().multiply(new BigDecimal(qtd));
                totalGeral = totalGeral.add(subtotal);

                ItemVenda item = new ItemVenda();
                item.setProduto(p);
                item.setQuantidade(qtd);
                item.setPrecoUnitarioHistorico(p.getPrecoVenda());
                listaItensVenda.add(item);

                modelTabela.addRow(new Object[]{
                    modelTabela.getRowCount() + 1,
                    p.getNome(),
                    qtd,
                    String.format("R$ %.2f", p.getPrecoVenda()),
                    String.format("R$ %.2f", subtotal)
                });

                lblTotal.setText(String.format("TOTAL: R$ %.2f", totalGeral));
                limparEntrada();
            } else {
                JOptionPane.showMessageDialog(this, "Produto não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void abrirModalPesquisa() {
        JDialog modal = new JDialog(this, "Pesquisa de Produtos", true);
        modal.setSize(600, 400);
        modal.setLocationRelativeTo(this);
        modal.setLayout(new BorderLayout());

        // Busca real de todos os produtos do estoque
        List<Produto> produtos = produtoService.listarTodos();
        String[] colunas = {"Código Serial", "Nome", "Preço", "Estoque"};
        DefaultTableModel modelBusca = new DefaultTableModel(colunas, 0);

        for (Produto p : produtos) {
            modelBusca.addRow(new Object[]{p.getCodigoSerial(), p.getNome(), p.getPrecoVenda(), p.getQuantidadeEstoque()});
        }

        JTable tabBusca = new JTable(modelBusca);
        tabBusca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    txtCodProduto.setText(tabBusca.getValueAt(tabBusca.getSelectedRow(), 0).toString());
                    modal.dispose();
                    txtCodProduto.requestFocus();
                }
            }
        });

        modal.add(new JScrollPane(tabBusca), BorderLayout.CENTER);
        modal.add(new JLabel(" Selecione o produto com duplo clique"), BorderLayout.SOUTH);
        modal.setVisible(true);
    }

    private void finalizarVenda() {
        if (listaItensVenda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum item adicionado!");
            return;
        }

        // Escolha do Método de Pagamento (Regra de Negócio)
        Venda.MetodoPagamento[] metodos = Venda.MetodoPagamento.values();
        Venda.MetodoPagamento selecao = (Venda.MetodoPagamento) JOptionPane.showInputDialog(
                this, "Selecione o Método de Pagamento:", "Finalizar Venda",
                JOptionPane.QUESTION_MESSAGE, null, metodos, metodos[0]);

        if (selecao != null) {
            try {
                Venda venda = new Venda();
                venda.setDataVenda(LocalDateTime.now());
                venda.setTotalVenda(totalGeral);
                venda.setUsuario(operador);
                venda.setMetodoPagamento(selecao);
                venda.setItens(listaItensVenda);

                // Persistência e Baixa de Estoque Real no PostgreSQL
                vendaService.realizarVenda(venda);

                int op = JOptionPane.showConfirmDialog(this, 
                    "Venda Concluída! Deseja imprimir o cupom?", 
                    "Sucesso", JOptionPane.YES_NO_OPTION);

                if (op == JOptionPane.YES_OPTION) {
                    System.out.println("LOG: Enviando layout do Cupom ID " + venda.getId() + " para processamento.");
                }

                resetarPDV();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao processar venda: " + ex.getMessage());
            }
        }
    }

    private void limparEntrada() {
        txtCodProduto.setText("");
        txtQtd.setText("1");
        txtCodProduto.requestFocus();
    }

    private void resetarPDV() {
        modelTabela.setRowCount(0);
        listaItensVenda.clear();
        totalGeral = BigDecimal.ZERO;
        lblTotal.setText("TOTAL: R$ 0,00");
        limparEntrada();
    }
}