package com.ERP_Varejo.view;

import com.ERP_Varejo.model.Usuario;
import com.ERP_Varejo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;

@Component
public class LoginView extends JFrame {

    @Autowired
    private UsuarioService usuarioService;

    private JTextField txtLogin;
    private JPasswordField txtSenha;

    public void exibir() {
        
        setTitle("ERP CasadosFogões - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1;
        txtLogin = new JTextField(15);
        panel.add(txtLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        txtSenha = new JPasswordField(15);
        panel.add(txtSenha, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton btnEntrar = new JButton("Acessar Sistema");
        btnEntrar.setBackground(new Color(41, 128, 185));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnEntrar.addActionListener(e -> realizarLogin());
        panel.add(btnEntrar, gbc);

        add(panel);
        setVisible(true);
    }

    private void realizarLogin() {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());

        Usuario usuario = usuarioService.autenticar(login, senha);

        if (usuario != null) {
            JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuario.getNome() + "!");
            this.dispose();
            
            SwingUtilities.invokeLater(() -> {
                MenuPrincipalView menu = new MenuPrincipalView(usuario);
                menu.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos!", "Erro de Acesso", JOptionPane.ERROR_MESSAGE);
        }
    }
}