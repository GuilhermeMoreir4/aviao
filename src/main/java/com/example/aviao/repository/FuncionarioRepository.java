package com.example.aviao.repository;

import com.example.aviao.model.Funcionario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FuncionarioRepository extends MongoRepository<Funcionario, String> {
    Optional<Funcionario> findByEmail(String email);
}
