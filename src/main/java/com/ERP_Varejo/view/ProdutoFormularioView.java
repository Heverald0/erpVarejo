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
        
        setTitle(produto == null ? "Novo Produto" : "Editar Produto");
        setSize(450, 480);
        setLocationRelativeTo(null);
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtNome = criarCampoPersonalizado(produto != null ? produto.getNome() : "");
        txtSerial = criarCampoPersonalizado(produto != null ? produto.getCodigoSerial() : "");
        txtCusto = criarCampoPersonalizado(produto != null && produto.getPrecoCusto() != null ? produto.getPrecoCusto().toString() : "0.00");
        txtVenda = criarCampoPersonalizado(produto != null && produto.getPrecoVenda() != null ? produto.getPrecoVenda().toString() : "0.00");
        txtEstoque = criarCampoPersonalizado(produto != null ? String.valueOf(produto.getQuantidadeEstoque()) : "0");

        if (produto != null) txtEstoque.setEditable(false);

        panel.add(new JLabel("Nome do Produto:")); panel.add(txtNome);
        panel.add(new JLabel("Código Serial:")); panel.add(txtSerial);
        panel.add(new JLabel("Preço de Custo:")); panel.add(txtCusto);
        panel.add(new JLabel("Preço de Venda:")); panel.add(txtVenda);
        panel.add(new JLabel("Estoque Atual:")); panel.add(txtEstoque);

        JButton btnSalvar = new JButton("CONFIRMAR (ENTER)");
        btnSalvar.setBackground(new Color(46, 204, 113));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.addActionListener(e -> salvar());

        add(panel, BorderLayout.CENTER);
        add(btnSalvar, BorderLayout.SOUTH);

        txtNome.requestFocus();
        setVisible(true);
    }

    private JTextField criarCampoPersonalizado(String texto) {
        JTextField field = new JTextField(texto);
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
            if (produtoExistente == null) p.setQuantidadeEstoque(Integer.parseInt(txtEstoque.getText()));

            produtoService.salvar(p);
            if (callbackAtualizacao != null) callbackAtualizacao.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }
}