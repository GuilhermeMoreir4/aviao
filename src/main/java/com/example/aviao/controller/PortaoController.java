package com.example.aviao.controller;

import com.example.aviao.model.Portao;
import com.example.aviao.service.PortaoService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/portoes")
public class PortaoController {

    private final PortaoService portaoService;

    public PortaoController(PortaoService portaoService) {
        this.portaoService = portaoService;
    }

    // Criar Portão - só admin
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping
    public ResponseEntity<?> criarPortao(@Valid @RequestBody Portao portao, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> erros = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(erros);
        }

        try {
            Portao novoPortao = portaoService.criarPortao(portao);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoPortao);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // Listar Todos os Portões - qualquer usuário autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<Portao>> listarTodos() {
        List<Portao> portoes = portaoService.listarTodos();
        return ResponseEntity.ok(portoes);
    }

    // Buscar Portão por ID - qualquer usuário autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<Portao> buscarPorId(@PathVariable String id) {
        Optional<Portao> portao = portaoService.buscarPorId(id);
        return portao.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Buscar Portão por Código - qualquer usuário autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Portao> buscarPorCodigo(@PathVariable String codigo) {
        Optional<Portao> portao = portaoService.buscarPorCodigo(codigo);
        return portao.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Buscar Portão Disponível - qualquer usuário autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/disponivel")
    public ResponseEntity<Portao> buscarDisponivel() {
        Optional<Portao> portao = portaoService.buscarDisponivel();
        return portao.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Atualizar Portão - só admin
    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable String id, @RequestBody Portao portao) {
        try {
            Portao atualizado = portaoService.atualizar(id, portao);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    // Deletar Portão - só admin
    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        portaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
