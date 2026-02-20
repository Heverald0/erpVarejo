package com.ERP_Varejo.view;

import com.ERP_Varejo.model.Usuario;
import javax.swing.*;
import java.awt.*;

public class MenuPrincipalView extends JFrame {

    public MenuPrincipalView(Usuario usuario, LoginView loginView) {
        setTitle("ERP CasadosFogões - Gestão");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // --- BARRA SUPERIOR (User & Logout) ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(26, 37, 47));
        topBar.setPreferredSize(new Dimension(1000, 60));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblBemVindo = new JLabel("Operador: " + usuario.getNome() + " | Perfil: " + usuario.getCargo());
        lblBemVindo.setForeground(Color.WHITE);
        lblBemVindo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton btnLogout = new JButton("Trocar Usuário (Sair)");
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            this.dispose();
            loginView.exibir(); // Retorna ao Login
        });

        topBar.add(lblBemVindo, BorderLayout.WEST);
        topBar.add(btnLogout, BorderLayout.EAST);

        // --- PAINEL CENTRAL (Dashboard & Ações) ---
        JPanel mainContent = new JPanel(new BorderLayout());
        
        // 1. Área de "Cards" com Informações Rápidas
        JPanel pnlDashboard = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlDashboard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        pnlDashboard.add(criarCardInfo("VENDAS HOJE", "R$ 0,00", new Color(46, 204, 113)));
        pnlDashboard.add(criarCardInfo("ITENS NO ESTOQUE", "Consultar", new Color(52, 152, 219)));
        pnlDashboard.add(criarCardInfo("ALERTAS", "Nenhum", new Color(231, 76, 60)));

        // 2. Área de Botões de Ação
        JPanel pnlAcoes = new JPanel(new GridLayout(2, 2, 15, 15));
        pnlAcoes.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        pnlAcoes.add(configurarBotaoMenu("CAIXA (F1)", true));
        pnlAcoes.add(configurarBotaoMenu("ESTOQUE", true));
        
        // Bloqueio administrativo
        boolean isAdmin = usuario.getCargo() == Usuario.Perfil.ADMIN;
        pnlAcoes.add(configurarBotaoMenu("RELATÓRIOS", isAdmin));
        pnlAcoes.add(configurarBotaoMenu("CONFIGURAÇÕES", isAdmin));

        mainContent.add(pnlDashboard, BorderLayout.NORTH);
        mainContent.add(pnlAcoes, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel criarCardInfo(String titulo, String valor, Color cor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cor);
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        JLabel lblT = new JLabel(titulo, SwingConstants.CENTER);
        lblT.setForeground(Color.WHITE);
        JLabel lblV = new JLabel(valor, SwingConstants.CENTER);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblV.setForeground(Color.WHITE);

        card.add(lblT, BorderLayout.NORTH);
        card.add(lblV, BorderLayout.CENTER);
        return card;
    }

    private JButton configurarBotaoMenu(String texto, boolean habilitado) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setEnabled(habilitado);
        if (!habilitado) {
            btn.setToolTipText("Acesso exclusivo para administradores.");
        }
        return btn;
    }
}