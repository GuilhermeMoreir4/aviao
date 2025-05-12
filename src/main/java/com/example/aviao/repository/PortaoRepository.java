package com.example.aviao.repository;


import com.example.aviao.model.Portao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PortaoRepository extends MongoRepository<Portao, String> {
    Optional<Portao> findByCodigo(String codigo);
    Optional<Portao> findByDisponivelTrue();
}
