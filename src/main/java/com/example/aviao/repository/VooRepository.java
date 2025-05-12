package com.example.aviao.repository;


import com.example.aviao.model.Voo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VooRepository extends MongoRepository<Voo, String> {
    List<Voo> findByDataHoraPartidaBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Voo> findByPortaoId(String portaoId);
}
