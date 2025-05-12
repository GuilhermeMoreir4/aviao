package com.example.aviao.controller;

import com.example.aviao.model.Passageiro;
import com.example.aviao.service.PassageiroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/passageiros")
public class PassageiroController {

    private final PassageiroService passageiroService;

    public PassageiroController(PassageiroService passageiroService) {
        this.passageiroService = passageiroService;
    }

    // Criar Passageiro
    @PostMapping
    public ResponseEntity<?> criarPassageiro(@Valid @RequestBody Passageiro passageiro, BindingResult result) {
        if (result.hasErrors()) {
            // Extrair todos os erros de campo
            Map<String, String> erros = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(erros);
        }

        try {
            Passageiro novoPassageiro = passageiroService.criarPassageiro(passageiro);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoPassageiro);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Listar Todos os Passageiros
    @GetMapping
    public ResponseEntity<List<Passageiro>> listarTodos() {
        List<Passageiro> passageiros = passageiroService.listarTodos();
        return ResponseEntity.ok(passageiros);
    }

    // Buscar Passageiro por ID
    @GetMapping("/{id}")
    public ResponseEntity<Passageiro> buscarPorId(@PathVariable String id) {
        Optional<Passageiro> passageiro = passageiroService.buscarPorId(id);
        return passageiro.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Atualizar Passageiro
    @PutMapping("/{id}")
    public ResponseEntity<Passageiro> atualizar(@PathVariable String id, @RequestBody Passageiro passageiro) {
        try {
            Passageiro atualizado = passageiroService.atualizar(id, passageiro);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Passageiro) Map.of("erro", e.getMessage()));
        }
    }

    // Deletar Passageiro
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        passageiroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Fazer Check-in do Passageiro
    @PostMapping("/{id}/checkin")
    public ResponseEntity<Passageiro> fazerCheckin(@PathVariable String id) {
        try {
            Passageiro passageiro = passageiroService.fazerCheckin(id);
            return ResponseEntity.ok(passageiro);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Listar Passageiros por Voo
    @GetMapping("/voo/{vooId}")
    public ResponseEntity<List<Passageiro>> listarPorVoo(@PathVariable String vooId) {
        List<Passageiro> passageiros = passageiroService.listarPorVoo(vooId);
        return ResponseEntity.ok(passageiros);
    }
}
