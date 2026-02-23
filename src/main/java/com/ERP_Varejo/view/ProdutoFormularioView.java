package com.ERP_Varejo.view;

import com.ERP_Varejo.model.Produto;
import com.ERP_Varejo.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

@Component
public class ProdutoFormularioView extends JDialog {

    @Autowired
    private ProdutoService produtoService;

    private JTextField txtNome, txtSerial, txtCusto, txtVenda, txtEstoque;
    private Runnable callbackAtualizacao;
    private Produto produtoExistente;

    public void exibir(Runnable callback, Produto produto) {
        this.callbackAtualizacao = callback;
        this.produtoExistente = produto;
        
        String titulo = (produto == null) ? "Novo Produto - CasadosFogões" : "Editar Produto - CasadosFogões";
        setTitle(titulo);
        setSize(450, 480);
        setLocationRelativeTo(null);
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtNome = criarCampoPersonalizado(produto != null ? produto.getNome() : "");
        txtSerial = criarCampoPersonalizado(produto != null ? produto.getCodigoSerial() : "");
        txtCusto = criarCampoPersonalizado(produto != null ? produto.getPrecoCusto().toString() : "0.00");
        txtVenda = criarCampoPersonalizado(produto != null ? produto.getPrecoVenda().toString() : "0.00");
        txtEstoque = criarCampoPersonalizado(produto != null ? produto.getQuantidadeEstoque().toString() : "0");

        if (produto != null) {
            txtEstoque.setEditable(false);
            txtEstoque.setToolTipText("Use o botão 'Entrada' na tela de gestão para alterar o saldo.");
        }

        panel.add(new JLabel("Nome do Produto:"));
        panel.add(txtNome);
        panel.add(new JLabel("Código:"));
        panel.add(txtSerial);
        panel.add(new JLabel("Preço de Custo (R$):"));
        panel.add(txtCusto);
        panel.add(new JLabel("Preço de Venda (R$):"));
        panel.add(txtVenda);
        panel.add(new JLabel("Qtd Atual em Estoque:"));
        panel.add(txtEstoque);

        JButton btnSalvar = new JButton("SALVAR ALTERAÇÕES (ENTER)");
        btnSalvar.setBackground(new Color(46, 204, 113));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvar.addActionListener(e -> salvar());

        add(panel, BorderLayout.CENTER);
        add(btnSalvar, BorderLayout.SOUTH);

        txtNome.requestFocus();
        setVisible(true);
    }

    private JTextField criarCampoPersonalizado(String textoInicial) {
        JTextField field = new JTextField(textoInicial);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { field.selectAll(); }
        });
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) salvar();
            }
        });
        return field;
    }

    private void salvar() {
        try {
            Produto p = (produtoExistente != null) ? produtoExistente : new Produto();
            p.setNome(txtNome.getText().trim());
            p.setCodigoSerial(txtSerial.getText().trim());
            p.setPrecoCusto(new BigDecimal(txtCusto.getText().replace(",", ".")));
            p.setPrecoVenda(new BigDecimal(txtVenda.getText().replace(",", ".")));
            
            // estoque manualmente somente se for produto novo
            if (produtoExistente == null) {
                p.setQuantidadeEstoque(Integer.parseInt(txtEstoque.getText().trim()));
            }

            produtoService.salvar(p);
            JOptionPane.showMessageDialog(this, "Produto salvo com sucesso!");
            
            if (callbackAtualizacao != null) callbackAtualizacao.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}