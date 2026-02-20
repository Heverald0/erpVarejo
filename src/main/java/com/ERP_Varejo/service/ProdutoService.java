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
        if (produto.getCodigoSerial() == null || produto.getCodigoSerial().isEmpty()) {
            String serialUnico = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            produto.setCodigoSerial("SN-" + serialUnico);
        }
        
        return repository.save(produto);
    }


    public Produto buscarPorSerial(String serial) {
        return repository.findByCodigoSerial(serial);
    }


    public List<Produto> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }


    public List<Produto> listarTodos() {
        return repository.findAll();
    }


    public void deletar(Integer id) {
        repository.deleteById(id);
    }
}