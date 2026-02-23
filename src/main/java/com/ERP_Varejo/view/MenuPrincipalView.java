package com.ERP_Varejo.view;

import com.ERP_Varejo.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
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

    public MenuPrincipalView(Usuario usuario, LoginView loginView, VendaView vendaView, ProdutoGestaoView produtoGestaoView) {
        this.usuario = usuario;
        this.loginView = loginView;
        this.vendaView = vendaView;
        this.produtoGestaoView = produtoGestaoView;

        setTitle("ERP CasadosFogões - Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        configurarLayout();
        configurarAtalhosGlobais();
    }

    private void configurarLayout() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(26, 37, 47));
        topBar.setPreferredSize(new Dimension(1000, 60));
        
        JLabel lblUser = new JLabel("  Operador: " + usuario.getNome() + " | Perfil: " + usuario.getCargo());
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton btnLogout = new JButton("Sair (ESC)");
        btnLogout.addActionListener(e -> realizarLogout());

        topBar.add(lblUser, BorderLayout.WEST);
        topBar.add(btnLogout, BorderLayout.EAST);

        JPanel pnlAcoes = new JPanel(new GridLayout(2, 2, 15, 15));
        pnlAcoes.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlAcoes.add(criarBotaoMenu("CAIXA (F1)", true, e -> abrirPDV()));
        
        pnlAcoes.add(criarBotaoMenu("ESTOQUE (F2)", true, e -> produtoGestaoView.exibir()));
        
        boolean isAdmin = usuario.getCargo() == Usuario.Perfil.ADMIN;
        pnlAcoes.add(criarBotaoMenu("RELATÓRIOS (F3)", isAdmin, e -> System.out.println("Relatórios")));
        pnlAcoes.add(criarBotaoMenu("CONFIG (F4)", isAdmin, e -> System.out.println("Config")));

        add(topBar, BorderLayout.NORTH);
        add(pnlAcoes, BorderLayout.CENTER);
    }

    private void configurarAtalhosGlobais() {
        JRootPane rootPane = this.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "abrirPDV");
        actionMap.put("abrirPDV", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { abrirPDV(); }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "abrirEstoque");
        actionMap.put("abrirEstoque", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { produtoGestaoView.exibir(); }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "logout");
        actionMap.put("logout", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { realizarLogout(); }
        });
    }

    private void abrirPDV() {
        vendaView.exibir(usuario);
    }

    private void realizarLogout() {
        this.dispose();
        loginView.exibir();
    }

    private JButton criarBotaoMenu(String texto, boolean habilitado, java.awt.event.ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setEnabled(habilitado);
        btn.addActionListener(acao);
        if (!habilitado) btn.setToolTipText("Acesso Restrito");
        return btn;
    }
}