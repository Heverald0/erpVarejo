package com.ERP_Varejo.view;

import com.ERP_Varejo.model.Produto;
import com.ERP_Varejo.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

@Component
public class ProdutoGestaoView extends JFrame {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoFormularioView formularioView;

    private JTable tabela;
    private DefaultTableModel model;
    private JTextField txtBusca;

    public void exibir() {
        setTitle("Gestão de Estoque - CasadosFogões");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pnlBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlBusca.setBackground(new Color(236, 240, 241));
        txtBusca = new JTextField(30);
        pnlBusca.add(new JLabel("Consultar Produto:"));
        pnlBusca.add(txtBusca);

        String[] colunas = {"ID", "Serial", "Nome", "Preço Venda", "Estoque"};
        model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(model);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton btnNovo = new JButton("+ NOVO");
        btnNovo.setBackground(new Color(52, 152, 219));
        btnNovo.setForeground(Color.WHITE);

        JButton btnEntrada = new JButton("ENTRADA (REMESSA)");
        btnEntrada.setBackground(new Color(155, 89, 182));
        btnEntrada.setForeground(Color.WHITE);

        JButton btnEditar = new JButton("EDITAR");
        btnEditar.setBackground(new Color(241, 196, 15));

        JButton btnExcluir = new JButton("EXCLUIR");
        btnExcluir.setBackground(new Color(231, 76, 60));
        btnExcluir.setForeground(Color.WHITE);

        pnlAcoes.add(btnNovo);
        pnlAcoes.add(btnEntrada);
        pnlAcoes.add(btnEditar);
        pnlAcoes.add(btnExcluir);

        add(pnlBusca, BorderLayout.NORTH);
        add(pnlAcoes, BorderLayout.SOUTH);

        btnNovo.addActionListener(e -> formularioView.exibir(() -> atualizarTabela(""), null));
        
        btnEditar.addActionListener(e -> {
            Produto p = getProdutoSelecionado();
            if (p != null) formularioView.exibir(() -> atualizarTabela(""), p);
        });

        btnEntrada.addActionListener(e -> {
            Produto p = getProdutoSelecionado();
            if (p != null) {
                String input = JOptionPane.showInputDialog(this, "Qtd de itens na nova remessa para: " + p.getNome());
                if (input != null && !input.isEmpty()) {
                    try {
                        int adicional = Integer.parseInt(input);
                        p.setQuantidadeEstoque(p.getQuantidadeEstoque() + adicional);
                        produtoService.salvar(p);
                        atualizarTabela("");
                        JOptionPane.showMessageDialog(this, "Estoque atualizado!");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Valor inválido!");
                    }
                }
            }
        });

        btnExcluir.addActionListener(e -> {
            Produto p = getProdutoSelecionado();
            if (p != null) {
                String senha = JOptionPane.showInputDialog(this, "Confirme a senha de ADMIN para excluir:");
                if ("admin123".equals(senha)) {
                    produtoService.excluir(p.getId());
                    atualizarTabela("");
                    JOptionPane.showMessageDialog(this, "Produto removido!");
                } else {
                    JOptionPane.showMessageDialog(this, "Senha incorreta!");
                }
            }
        });
        
        txtBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { atualizarTabela(txtBusca.getText()); }
        });

        atualizarTabela("");
        setVisible(true);
    }

    private Produto getProdutoSelecionado() {
        int row = tabela.getSelectedRow();
        if (row != -1) {
            Integer id = (Integer) model.getValueAt(row, 0);
            return produtoService.buscarPorId(id);
        }
        JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.");
        return null;
    }

    private void atualizarTabela(String filtro) {
        List<Produto> lista = produtoService.buscarPorNome(filtro);
        model.setRowCount(0);
        for (Produto p : lista) {
            model.addRow(new Object[]{p.getId(), p.getCodigoSerial(), p.getNome(), p.getPrecoVenda(), p.getQuantidadeEstoque()});
        }
    }
}