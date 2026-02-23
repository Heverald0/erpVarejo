package com.ERP_Varejo.view;

import com.ERP_Varejo.model.Produto;
import com.ERP_Varejo.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
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
        add(pnlBusca, BorderLayout.NORTH);

        String[] colunas = {"ID", "Serial", "Nome", "Preço Venda", "Estoque"};
        model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(model);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton btnNovo = criarBotao("+ NOVO", new Color(52, 152, 219));
        JButton btnEntrada = criarBotao("ENTRADA (REMESSA)", new Color(155, 89, 182));
        JButton btnEditar = criarBotao("EDITAR", new Color(241, 196, 15));
        JButton btnExcluir = criarBotao("EXCLUIR", new Color(231, 76, 60));

        pnlAcoes.add(btnNovo); 
        pnlAcoes.add(btnEntrada); 
        pnlAcoes.add(btnEditar); 
        pnlAcoes.add(btnExcluir);
        add(pnlAcoes, BorderLayout.SOUTH);

        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fecharJanela");
        
        this.getRootPane().getActionMap().put("fecharJanela", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnNovo.addActionListener(e -> formularioView.exibir(() -> atualizarTabela(""), null));

        btnEditar.addActionListener(e -> {
            Produto p = getProdutoSelecionado();
            if (p != null) formularioView.exibir(() -> atualizarTabela(""), p);
        });

        btnEntrada.addActionListener(e -> realizarEntradaRemessa());

        btnExcluir.addActionListener(e -> {
            Produto p = getProdutoSelecionado();
            if (p != null) {
                String senha = JOptionPane.showInputDialog(this, "Confirme a senha de ADMIN para excluir:");
                if ("admin123".equals(senha)) {
                    produtoService.excluir(p.getId());
                    atualizarTabela("");
                    JOptionPane.showMessageDialog(this, "Produto removido com sucesso.");
                } else {
                    JOptionPane.showMessageDialog(this, "Senha incorreta! Operação cancelada.");
                }
            }
        });

        txtBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                atualizarTabela(txtBusca.getText());
            }
        });

        atualizarTabela("");
        setVisible(true);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        if (cor != Color.YELLOW) btn.setForeground(Color.WHITE);
        btn.setFocusable(false);
        return btn;
    }

    private void realizarEntradaRemessa() {
        Produto p = getProdutoSelecionado();
        if (p != null) {
            String input = JOptionPane.showInputDialog(this, 
                "Quantidade recebida para: " + p.getNome() + "\nSaldo atual: " + p.getQuantidadeEstoque());
            
            if (input != null && !input.isEmpty()) {
                try {
                    int adicional = Integer.parseInt(input.trim());
                    if (adicional <= 0) {
                        JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.");
                        return;
                    }
                    p.setQuantidadeEstoque(p.getQuantidadeEstoque() + adicional);
                    produtoService.salvar(p);
                    atualizarTabela(txtBusca.getText());
                    JOptionPane.showMessageDialog(this, "Estoque atualizado!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Valor inválido! Digite apenas números.");
                }
            }
        }
    }

    private Produto getProdutoSelecionado() {
        int row = tabela.getSelectedRow();
        if (row != -1) {
            int modelRow = tabela.convertRowIndexToModel(row);
            Integer id = (Integer) model.getValueAt(modelRow, 0);
            return produtoService.buscarPorId(id);
        }
        JOptionPane.showMessageDialog(this, "Por favor, selecione um produto na tabela primeiro.");
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