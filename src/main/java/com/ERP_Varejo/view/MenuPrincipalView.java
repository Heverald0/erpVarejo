package com.ERP_Varejo.view;

import com.ERP_Varejo.model.Usuario;
import javax.swing.*;
import java.awt.*;

public class MenuPrincipalView extends JFrame {

    public MenuPrincipalView(Usuario usuario) {
        setTitle("ERP CasadosFogões - Menu Principal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        JPanel pnlBotoes = new JPanel(new GridLayout(2, 2, 20, 20));
        pnlBotoes.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JButton btnVendas = new JButton("PDV - Realizar Venda");
        JButton btnProdutos = new JButton("Estoque / Produtos");
        JButton btnRelatorios = new JButton("Relatórios Financeiros");
        JButton btnConfig = new JButton("Gestão de Usuários");

        Font fonteBotao = new Font("Arial", Font.BOLD, 16);
        btnVendas.setFont(fonteBotao);
        btnProdutos.setFont(fonteBotao);
        btnRelatorios.setFont(fonteBotao);
        btnConfig.setFont(fonteBotao);

        // --- LÓGICA DE PERMISSÕES (BLOQUEIO) ---
        // Verifica se o cargo do usuário é diferente de ADMIN
        if (usuario.getCargo() != Usuario.Perfil.ADMIN) {
            btnRelatorios.setEnabled(false);
            btnConfig.setEnabled(false);
            
            String msgAviso = "Acesso restrito a administradores.";
            btnRelatorios.setToolTipText(msgAviso);
            btnConfig.setToolTipText(msgAviso);
            
            btnRelatorios.setBackground(Color.LIGHT_GRAY);
            btnConfig.setBackground(Color.LIGHT_GRAY);
        }

        pnlBotoes.add(btnVendas);
        pnlBotoes.add(btnProdutos);
        pnlBotoes.add(btnRelatorios);
        pnlBotoes.add(btnConfig);

        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlStatus.setBorder(BorderFactory.createEtchedBorder());
        JLabel lblUser = new JLabel("Usuário: " + usuario.getNome() + " | Perfil: " + usuario.getCargo());
        pnlStatus.add(lblUser);

        add(pnlBotoes, BorderLayout.CENTER);
        add(pnlStatus, BorderLayout.SOUTH);
    }
}