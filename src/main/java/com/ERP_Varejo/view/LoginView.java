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
    private ProdutoGestaoView produtoGestaoView;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private VendaView vendaView;

    private JTextField txtLogin;
    private JPasswordField txtSenha;

    public void exibir() {
        if (txtLogin != null) txtLogin.setText("");
        if (txtSenha != null) txtSenha.setText("");

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }

        setTitle("Acesso ao Sistema - ERP CasadosFogões");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(44, 62, 80));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel l1 = new JLabel("Usuário:"); l1.setForeground(Color.WHITE);
        formPanel.add(l1, gbc);
        
        gbc.gridx = 1;
        txtLogin = new JTextField(15);
        formPanel.add(txtLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel l2 = new JLabel("Senha:"); l2.setForeground(Color.WHITE);
        formPanel.add(l2, gbc);
        
        gbc.gridx = 1;
        txtSenha = new JPasswordField(15);
        formPanel.add(txtSenha, gbc);

        JButton btnEntrar = new JButton("ENTRAR");
        btnEntrar.setBackground(new Color(52, 152, 219));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.addActionListener(e -> realizarLogin());

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(btnEntrar, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    
        if (txtLogin != null) txtLogin.requestFocus();
    }

    private void realizarLogin() {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());
        Usuario auth = usuarioService.autenticar(login, senha);

        if (auth != null) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new MenuPrincipalView(auth, this, vendaView, produtoGestaoView).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(this, "Acesso Negado!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}