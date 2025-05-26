package com.example.aviao.controller;

import com.example.aviao.dto.FuncionarioDTO;
import com.example.aviao.dto.LoginDTO;
import com.example.aviao.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private FuncionarioService service;

    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrar(@RequestBody FuncionarioDTO dto) {
        service.cadastrar(dto);
        return ResponseEntity.ok("Funcionário cadastrado");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO dto) {
        System.out.println("Hi hitler");
        String token = service.login(dto);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
