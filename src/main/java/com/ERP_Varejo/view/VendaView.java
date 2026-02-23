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

        String[] colunas = {"Item", "Descrição", "Qtd", "V. Unit", "Subtotal"};
        modelTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaItens = new JTable(modelTabela);
        tabelaItens.setRowHeight(30);
        tabelaItens.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

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
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    adicionarItem();
                } else if (e.getKeyCode() == KeyEvent.VK_F2) {
                    abrirModalPesquisa();
                } else if (e.getKeyCode() == KeyEvent.VK_F5) {
                    finalizarVenda();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        };
        txtCodProduto.addKeyListener(adapter);
        txtQtd.addKeyListener(adapter);
    }

    private void adicionarItem() {
        try {
            String codigo = txtCodProduto.getText().trim();
            if (codigo.isEmpty()) return;

            Produto p = produtoService.buscarPorSerial(codigo);

            if (p != null) {
                int qtd = Integer.parseInt(txtQtd.getText());
                
                if (p.getQuantidadeEstoque() < qtd) {
                    JOptionPane.showMessageDialog(this, "Estoque insuficiente! Disponível: " + p.getQuantidadeEstoque());
                    return;
                }

                BigDecimal preco = p.getPrecoVenda();
                BigDecimal subtotal = preco.multiply(new BigDecimal(qtd));
                totalGeral = totalGeral.add(subtotal);

                ItemVenda item = new ItemVenda();
                item.setProduto(p);
                item.setQuantidade(qtd);
                item.setPrecoUnitarioHistorico(preco);
                listaItensVenda.add(item);

                modelTabela.addRow(new Object[]{
                    modelTabela.getRowCount() + 1,
                    p.getNome(),
                    qtd,
                    String.format("R$ %.2f", preco),
                    String.format("R$ %.2f", subtotal)
                });

                lblTotal.setText(String.format("TOTAL: R$ %.2f", totalGeral));
                limparEntrada();
            } else {
                JOptionPane.showMessageDialog(this, "Produto '" + codigo + "' não encontrado!", "Erro de Busca", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser um número inteiro.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro técnico: " + ex.getMessage());
        }
    }

    private void abrirModalPesquisa() {
        JDialog modal = new JDialog(this, "Pesquisa de Produtos", true);
        modal.setSize(700, 450);
        modal.setLocationRelativeTo(this);
        modal.setLayout(new BorderLayout());

        JPanel pnlBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtFiltro = new JTextField(20);
        pnlBusca.add(new JLabel("Filtrar Nome:"));
        pnlBusca.add(txtFiltro);
        modal.add(pnlBusca, BorderLayout.NORTH);

        List<Produto> produtos = produtoService.listarTodos();
        String[] colunas = {"Código Serial", "Nome", "Preço", "Estoque"};
        DefaultTableModel modelBusca = new DefaultTableModel(colunas, 0);

        for (Produto p : produtos) {
            modelBusca.addRow(new Object[]{p.getCodigoSerial(), p.getNome(), p.getPrecoVenda(), p.getQuantidadeEstoque()});
        }

        JTable tabBusca = new JTable(modelBusca);
        modal.add(new JScrollPane(tabBusca), BorderLayout.CENTER);

        tabBusca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    txtCodProduto.setText(tabBusca.getValueAt(tabBusca.getSelectedRow(), 0).toString());
                    modal.dispose();
                    SwingUtilities.invokeLater(() -> adicionarItem());
                }
            }
        });

        modal.setVisible(true);
    }

    private void finalizarVenda() {
        if (listaItensVenda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio!");
            return;
        }

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

                vendaService.realizarVenda(venda);

                JOptionPane.showMessageDialog(this, "Venda finalizada com sucesso!");
                resetarPDV();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar venda: " + ex.getMessage());
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