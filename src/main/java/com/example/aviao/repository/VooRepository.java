package com.example.aviao.repository;


import com.example.aviao.model.Voo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VooRepository extends MongoRepository<Voo, String> {
    List<Voo> findByDataHoraPartidaBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Voo> findByPortaoId(String portaoId);

    boolean existsByNumeroVoo(@NotBlank(message = "O número do voo é obrigatório") @Pattern(
            regexp = "^[A-Z]{2}\\d{3,4}$",
            message = "O número do voo deve seguir o padrão (ex: AZ1234)"
    ) String numeroVoo);

}
