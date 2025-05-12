package com.example.aviao.service;


import com.example.aviao.model.Portao;
import com.example.aviao.repository.PortaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortaoService {

    private final PortaoRepository portaoRepo;

    public PortaoService(PortaoRepository portaoRepo) {
        this.portaoRepo = portaoRepo;
    }

    public Portao criarPortao(Portao portao) {
        // Código deve ser único
        portaoRepo.findByCodigo(portao.getCodigo()).ifPresent(p -> {
            throw new IllegalArgumentException("Código de portão já existe.");
        });

        portao.setDisponivel(true); // Sempre começa disponível
        return portaoRepo.save(portao);
    }

    public List<Portao> listarTodos() {
        return portaoRepo.findAll();
    }

    public Optional<Portao> buscarPorId(String id) {
        return portaoRepo.findById(id);
    }

    public Optional<Portao> buscarPorCodigo(String codigo) {
        return portaoRepo.findByCodigo(codigo);
    }

    public Optional<Portao> buscarDisponivel() {
        return portaoRepo.findByDisponivelTrue();
    }

    public void deletar(String id) {
        portaoRepo.deleteById(id);
    }

    public Portao atualizar(String id, Portao atualizado) {
        Portao existente = portaoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Portão não encontrado."));

        if (!existente.getCodigo().equalsIgnoreCase(atualizado.getCodigo())) {
            portaoRepo.findByCodigo(atualizado.getCodigo()).ifPresent(p -> {
                throw new IllegalArgumentException("Já existe um portão com esse código.");
            });
        }

        existente.setCodigo(atualizado.getCodigo());
        existente.setDisponivel(atualizado.isDisponivel());

        return portaoRepo.save(existente);
    }
}

