package com.example.aviao.security;

import com.example.aviao.model.Funcionario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}") // Injeta a chave do application.properties
    private String secret;
    private final long expiracao = 2 * 60 * 1000; // 2 minutos

    public String gerarToken(Funcionario func) {
        return Jwts.builder()
                .setSubject(func.getId())
                .claim("nome", func.getNome())
                .claim("cargo", func.getCargo())
                .setExpiration(new Date(System.currentTimeMillis() + expiracao))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Claims validarToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
