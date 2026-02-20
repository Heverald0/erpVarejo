package com.ERP_Varejo.view;

import com.ERP_Varejo.model.Usuario;
import javax.swing.*;
import java.awt.*;

public class MenuPrincipalView extends JFrame {

    public MenuPrincipalView(Usuario usuario, LoginView loginView) {
        setTitle("ERP CasadosFogões - Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Barra Superior de Usuário
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(52, 73, 94));
        topBar.setPreferredSize(new Dimension(900, 50));

        JLabel userLabel = new JLabel("  Usuário: " + usuario.getNome() + " (" + usuario.getCargo() + ")");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        
        JButton btnLogout = new JButton("Sair / Trocar Usuário");
        btnLogout.addActionListener(e -> {
            this.dispose();
            loginView.exibir(); // Retorna para a tela de login
        });

        topBar.add(userLabel, BorderLayout.WEST);
        topBar.add(btnLogout, BorderLayout.EAST);

        // Painel Central de Ações (Estilo Cards)
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        cardsPanel.add(criarBotaoMenu("PDV - Vendas", new Color(46, 204, 113), true));
        cardsPanel.add(criarBotaoMenu("Produtos", new Color(241, 196, 15), true));
        
        // Funções Administrativas
        boolean isAdmin = usuario.getCargo() == Usuario.Perfil.ADMIN;
        cardsPanel.add(criarBotaoMenu("Relatórios", new Color(231, 76, 60), isAdmin));
        cardsPanel.add(criarBotaoMenu("Configurações", new Color(149, 165, 166), isAdmin));

        add(topBar, BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);
    }

    private JButton criarBotaoMenu(String texto, Color cor, boolean habilitado) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBackground(habilitado ? cor : Color.LIGHT_GRAY);
        btn.setForeground(Color.WHITE);
        btn.setEnabled(habilitado);
        btn.setFocusPainted(false);
        if (!habilitado) btn.setToolTipText("Acesso Administrativo");
        return btn;
    }
}