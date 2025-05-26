package com.example.aviao.controller;

import com.example.aviao.model.Passageiro;
import com.example.aviao.repository.VooRepository;
import com.example.aviao.service.PassageiroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/passageiros")
public class PassageiroController {

    @Autowired
    private final PassageiroService passageiroService;

    @Autowired
    private final VooRepository vooRepository;

    public PassageiroController(PassageiroService passageiroService, VooRepository vooRepository) {
        this.passageiroService = passageiroService;
        this.vooRepository = vooRepository;
    }

    // Criar Passageiro - só admin
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping
    public ResponseEntity<?> criarPassageiro(@Valid @RequestBody Passageiro passageiro, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> erros = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(erros);
        }

        if (!vooRepository.existsById(passageiro.getVooId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Voo informado não existe."));
        }

        try {
            Passageiro novoPassageiro = passageiroService.criarPassageiro(passageiro);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoPassageiro);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Listar Todos os Passageiros - autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<Passageiro>> listarTodos() {
        List<Passageiro> passageiros = passageiroService.listarTodos();
        return ResponseEntity.ok(passageiros);
    }

    // Buscar Passageiro por ID - autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        Optional<Passageiro> passageiro = passageiroService.buscarPorId(id);
        return passageiro.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body((Passageiro) Map.of("erro", "Passageiro não encontrado.")));
    }

    // Atualizar Passageiro - só admin
    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable String id, @RequestBody Passageiro passageiro) {
        if (!vooRepository.existsById(passageiro.getVooId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Voo informado não existe."));
        }

        try {
            Passageiro atualizado = passageiroService.atualizar(id, passageiro);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Deletar Passageiro - só admin
    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable String id) {
        if (!passageiroService.buscarPorId(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Passageiro não encontrado."));
        }

        passageiroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Fazer Check-in do Passageiro - autenticado
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/checkin")
    public ResponseEntity<?> fazerCheckin(@PathVariable String id) {
        try {
            Passageiro atualizado = passageiroService.fazerCheckin(id);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    // Listar Passageiros por Voo - autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/voo/{vooId}")
    public ResponseEntity<?> listarPorVoo(@PathVariable String vooId) {
        if (!vooRepository.existsById(vooId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Voo não encontrado."));
        }

        List<Passageiro> passageiros = passageiroService.listarPorVoo(vooId);
        return ResponseEntity.ok(passageiros);
    }
}
