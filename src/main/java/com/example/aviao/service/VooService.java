package com.example.aviao.service;

import com.example.aviao.model.Portao;
import com.example.aviao.model.Voo;
import com.example.aviao.repository.PortaoRepository;
import com.example.aviao.repository.VooRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VooService {

    private final VooRepository vooRepo;
    private final PortaoRepository portaoRepo;

    public VooService(VooRepository vooRepo, PortaoRepository portaoRepo) {
        this.vooRepo = vooRepo;
        this.portaoRepo = portaoRepo;
    }

    public boolean portaoExiste(String portaoId) {
        return portaoRepo.existsById(portaoId);
    }

    public boolean numeroVooExiste(String numero) {
        return vooRepo.existsByNumeroVoo(numero);
    }


    public Voo criarVoo(Voo voo) {
        voo.setStatus("programado");
        return vooRepo.save(voo);
    }

    public List<Voo> listarTodos() {
        return vooRepo.findAll();
    }

    public Optional<Voo> buscarPorId(String id) {
        return vooRepo.findById(id);
    }

    public void deletar(String id) {
        vooRepo.deleteById(id);
    }

    public Voo atualizar(String id, Voo atualizado) {
        Voo existente = vooRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Voo não encontrado."));

        existente.setNumeroVoo(atualizado.getNumeroVoo());
        existente.setOrigem(atualizado.getOrigem());
        existente.setDestino(atualizado.getDestino());
        existente.setDataHoraPartida(atualizado.getDataHoraPartida());

        return vooRepo.save(existente);
    }

    public Voo atualizarStatus(String idVoo, String novoStatus) {
        Voo voo = vooRepo.findById(idVoo)
                .orElseThrow(() -> new IllegalArgumentException("Voo não encontrado."));

        if (!List.of("programado", "embarque", "concluido").contains(novoStatus)) {
            throw new IllegalArgumentException("Status inválido.");
        }

        voo.setStatus(novoStatus);
        Voo atualizado = vooRepo.save(voo);

        // Se o status for "concluido", libera o portão
        if ("concluido".equalsIgnoreCase(novoStatus)) {
            liberarPortao(voo.getPortaoId());
        }

        return atualizado;
    }

    public Voo atribuirPortao(String idVoo, String idPortao) {
        Voo voo = vooRepo.findById(idVoo)
                .orElseThrow(() -> new IllegalArgumentException("Voo não encontrado."));
        Portao portao = portaoRepo.findById(idPortao)
                .orElseThrow(() -> new IllegalArgumentException("Portão não encontrado."));

        if (!portao.isDisponivel()) {
            throw new IllegalStateException("Portão indisponível.");
        }

        voo.setPortaoId(portao.getId());
        portao.setDisponivel(false);

        portaoRepo.save(portao);
        return vooRepo.save(voo);
    }

    private void liberarPortao(String idPortao) {
        if (idPortao == null) return;

        portaoRepo.findById(idPortao).ifPresent(portao -> {
            portao.setDisponivel(true);
            portaoRepo.save(portao);
        });
    }

    public List<Voo> listarVoosDoDia() {
        // Assumindo o uso do horário local para delimitar os voos do dia
        var agora = java.time.LocalDateTime.now();
        var inicio = agora.toLocalDate().atStartOfDay();
        var fim = inicio.plusDays(1).minusSeconds(1);
        return vooRepo.findByDataHoraPartidaBetween(inicio, fim);
    }

    // Verificar se o portão está disponível para o voo no dia
    public boolean verificarDisponibilidadePortao(String portaoId, LocalDateTime dataHoraPartida) {
        // Buscar todos os voos no mesmo dia
        List<Voo> voosDoDia = vooRepo.findByDataHoraPartidaBetween(
                dataHoraPartida.toLocalDate().atStartOfDay(),
                dataHoraPartida.toLocalDate().atTime(23, 59, 59));

        // Verificar se algum voo já tem o portão atribuído
        for (Voo voo : voosDoDia) {
            if (voo.getPortaoId().equals(portaoId)) {
                return false; // Portão já está atribuído a outro voo no dia
            }
        }
        return true; // O portão está disponível
    }
}
