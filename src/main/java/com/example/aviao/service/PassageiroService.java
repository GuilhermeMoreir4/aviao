package com.example.aviao.service;


import com.example.aviao.model.Passageiro;
import com.example.aviao.model.Voo;
import com.example.aviao.repository.PassageiroRepository;
import com.example.aviao.repository.VooRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PassageiroService {

    @Autowired
    private final PassageiroRepository passageiroRepo;
    @Autowired
    private final VooRepository vooRepo;

    public PassageiroService(PassageiroRepository passageiroRepo, VooRepository vooRepo) {
        this.passageiroRepo = passageiroRepo;
        this.vooRepo = vooRepo;
    }

    public Passageiro criarPassageiro(Passageiro passageiro) {
        // CPF deve ser único
        passageiroRepo.findByCpf(passageiro.getCpf()).ifPresent(p -> {
            throw new IllegalArgumentException("CPF já cadastrado.");
        });

        // Valida se o voo existe
        Voo voo = vooRepo.findById(passageiro.getVooId())
                .orElseThrow(() -> new IllegalArgumentException("Voo não encontrado."));

        passageiro.setStatusCheckin("pendente");
        return passageiroRepo.save(passageiro);
    }

    public List<Passageiro> listarTodos() {
        return passageiroRepo.findAll();
    }

    public Optional<Passageiro> buscarPorId(String id) {
        return passageiroRepo.findById(id);
    }

    public void deletar(String id) {
        passageiroRepo.deleteById(id);
    }

    public Passageiro atualizar(String id, Passageiro atualizado) {
        Passageiro existente = passageiroRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Passageiro não encontrado."));

        if (!existente.getCpf().equals(atualizado.getCpf())) {
            passageiroRepo.findByCpf(atualizado.getCpf()).ifPresent(p -> {
                throw new IllegalArgumentException("Outro passageiro já usa este CPF.");
            });
        }

        existente.setNome(atualizado.getNome());
        existente.setCpf(atualizado.getCpf());
        existente.setVooId(atualizado.getVooId());
        return passageiroRepo.save(existente);
    }
    public Passageiro fazerCheckin(String idPassageiro) {
        Passageiro passageiro = passageiroRepo.findById(idPassageiro)
                .orElseThrow(() -> new IllegalArgumentException("Passageiro não encontrado."));

        Voo voo = vooRepo.findById(passageiro.getVooId())
                .orElseThrow(() -> new IllegalArgumentException("Voo não encontrado."));

        if (!"embarque".equalsIgnoreCase(voo.getStatus())) {
            throw new IllegalStateException("Check-in só é permitido quando o voo estiver com status 'embarque'.");
        }

        passageiro.setStatusCheckin("realizado");
        return passageiroRepo.save(passageiro);
    }

    public List<Passageiro> listarPorVoo(String vooId) {
        return passageiroRepo.findByVooId(vooId);
    }
}
