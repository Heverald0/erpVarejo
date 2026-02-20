package com.ERP_Varejo.service;

import com.ERP_Varejo.model.Usuario;
import com.ERP_Varejo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario autenticar(String login, String senhaPlana) {
        Usuario usuario = repository.findByLogin(login);
        
        // Comparação das senhas (digitada e salvas no bd)
        if (usuario != null && passwordEncoder.matches(senhaPlana, usuario.getSenha())) {
            return usuario;
        }
        return null;
    }

    public Usuario salvar(Usuario usuario) {
        // Criptografa a senha antes de mandar para o PostgreSQL
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        return repository.save(usuario);
    }
}