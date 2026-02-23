package com.ERP_Varejo.view;

import com.ERP_Varejo.model.Usuario;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class MenuPrincipalView extends JFrame {

    private final Usuario usuario;
    private final LoginView loginView;
    private final VendaView vendaView;
    private final ProdutoGestaoView produtoGestaoView;
    private final RelatorioCaixaView relatorioCaixaView;

    public MenuPrincipalView(Usuario usuario, LoginView loginView, VendaView vendaView, 
                               ProdutoGestaoView produtoGestaoView, RelatorioCaixaView relatorioCaixaView) {
        this.usuario = usuario;
        this.loginView = loginView;
        this.vendaView = vendaView;
        this.produtoGestaoView = produtoGestaoView;
        this.relatorioCaixaView = relatorioCaixaView;

        configurarJanela();
        configurarLayout();
        configurarAtalhosGlobais();
    }

    private void configurarJanela() {
        setTitle("Sistema ERP - CasadosFogões | Operador: " + usuario.getNome());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void configurarLayout() {
        setLayout(new BorderLayout());

        JPanel pnlCabecalho = new JPanel(new BorderLayout());
        pnlCabecalho.setBackground(new Color(44, 62, 80));
        pnlCabecalho.setPreferredSize(new Dimension(0, 80));

        JLabel lblTitulo = new JLabel(" ERP VAREJO - CASADOSFOGÕES", JLabel.LEFT);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pnlCabecalho.add(lblTitulo, BorderLayout.WEST);

        add(pnlCabecalho, BorderLayout.NORTH);

        JPanel pnlAcoes = new JPanel(new GridLayout(2, 2, 20, 20));
        pnlAcoes.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        boolean isAdmin = usuario.getCargo() == Usuario.Perfil.ADMIN;

        pnlAcoes.add(criarBotaoMenu("PDV - VENDAS (F1)", true, e -> vendaView.exibir(usuario)));
        pnlAcoes.add(criarBotaoMenu("ESTOQUE / PRODUTOS (F2)", true, e -> produtoGestaoView.exibir()));
        
        pnlAcoes.add(criarBotaoMenu("RELATÓRIOS (F3)", isAdmin, e -> {
            if (isAdmin) relatorioCaixaView.exibir();
            else JOptionPane.showMessageDialog(this, "Acesso restrito ao Administrador.");
        }));

        pnlAcoes.add(criarBotaoMenu("SAIR (ESC)", true, e -> fecharSistema()));

        add(pnlAcoes, BorderLayout.CENTER);
    }

    private JButton criarBotaoMenu(String texto, boolean habilitado, java.awt.event.ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setEnabled(habilitado);
        btn.setFocusable(false);
        btn.addActionListener(acao);
        return btn;
    }

    private void configurarAtalhosGlobais() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "abrirVendas");
        actionMap.put("abrirVendas", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { vendaView.exibir(usuario); }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "abrirEstoque");
        actionMap.put("abrirEstoque", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { produtoGestaoView.exibir(); }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "abrirRelatorios");
        actionMap.put("abrirRelatorios", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usuario.getCargo() == Usuario.Perfil.ADMIN) {
                    relatorioCaixaView.exibir();
                } else {
                    JOptionPane.showMessageDialog(null, "Acesso restrito ao Administrador.");
                }
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "sair");
        actionMap.put("sair", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { fecharSistema(); }
        });
    }

    private void fecharSistema() {
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente sair?", "Sair", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}