package com.example.aviao.controller;

import com.example.aviao.model.Voo;
import com.example.aviao.repository.PortaoRepository;
import com.example.aviao.service.VooService;
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
@RequestMapping("/api/voos")
public class VooController {

    private final VooService vooService;

    public VooController(VooService vooService, PortaoRepository portaoRepository) {
        this.vooService = vooService;
    }

    // Criar voo - só admin
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping
    public ResponseEntity<?> criarVoo(@Valid @RequestBody Voo voo, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> erros = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(erros);
        }

        try {
            if (vooService.numeroVooExiste(voo.getNumeroVoo())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("erro", "Número do voo já está em uso."));
            }
            if (!vooService.portaoExiste(voo.getPortaoId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Portão informado não existe."));
            }
            if (!vooService.verificarDisponibilidadePortao(voo.getPortaoId(), voo.getDataHoraPartida())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Portão já está atribuído a outro voo nesse dia."));
            }
            Voo novoVoo = vooService.criarVoo(voo);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoVoo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // Listar todos os voos - qualquer autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<Voo>> listarTodos() {
        List<Voo> voos = vooService.listarTodos();
        return ResponseEntity.ok(voos);
    }

    // Buscar voo por ID - qualquer autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<Voo> buscarPorId(@PathVariable String id) {
        Optional<Voo> voo = vooService.buscarPorId(id);
        return voo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Deletar voo - só admin
    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        vooService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Atualizar voo - só admin
    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable String id, @RequestBody Voo voo) {
        try {
            Voo atualizado = vooService.atualizar(id, voo);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    // Atualizar status do voo - só admin
    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable String id, @RequestParam String status) {
        try {
            Voo atualizado = vooService.atualizarStatus(id, status);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    // Atribuir portão ao voo - só admin
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/{idVoo}/portao/{idPortao}")
    public ResponseEntity<?> atribuirPortao(@PathVariable String idVoo, @PathVariable String idPortao) {
        try {
            Voo vooAtualizado = vooService.atribuirPortao(idVoo, idPortao);
            return ResponseEntity.ok(vooAtualizado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    // Listar voos do dia - qualquer autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/do-dia")
    public ResponseEntity<List<Voo>> listarVoosDoDia() {
        List<Voo> voos = vooService.listarVoosDoDia();
        return ResponseEntity.ok(voos);
    }
}
