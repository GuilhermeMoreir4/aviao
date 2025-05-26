package com.example.aviao.service;

import com.example.aviao.dto.FuncionarioDTO;
import com.example.aviao.dto.LoginDTO;
import com.example.aviao.model.Funcionario;
import com.example.aviao.repository.FuncionarioRepository;
import com.example.aviao.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;


@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    public void cadastrar(FuncionarioDTO dto) {
        if (repo.findByEmail(dto.email).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        Funcionario func = new Funcionario();
        func.setNome(dto.nome);
        func.setEmail(dto.email);
        func.setSenha(encoder.encode(dto.senha));
        func.setCargo(dto.cargo);

        repo.save(func);
    }

    public String login(LoginDTO dto) {
        Funcionario func = repo.findByEmail(dto.email)
                .orElseThrow(() -> new RuntimeException("Email inválido"));

        if (!encoder.matches(dto.senha, func.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        return jwtUtil.gerarToken(func);
    }
}
