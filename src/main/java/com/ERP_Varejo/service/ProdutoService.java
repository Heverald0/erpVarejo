package com.ERP_Varejo.service;

import com.ERP_Varejo.model.Produto;
import com.ERP_Varejo.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    public Produto salvar(Produto produto) {
        if (produto.getCodigoSerial() == null || produto.getCodigoSerial().trim().isEmpty()) {
            String prefixo = "PROD-";
            String sufixo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            produto.setCodigoSerial(prefixo + sufixo);
        }
        return repository.save(produto);
    }

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Produto buscarPorSerial(String serial) {
        return repository.findByCodigoSerial(serial);
    }

    public List<Produto> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public void excluir(Integer id) {
        repository.deleteById(id);
    }

    public Produto buscarPorId(Integer id) {
    return repository.findById(id).orElse(null);
}
}