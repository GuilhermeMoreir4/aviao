package com.example.aviao.repository;


import com.example.aviao.model.Passageiro;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PassageiroRepository extends MongoRepository<Passageiro, String> {
    Optional<Passageiro> findByCpf(String cpf);
    List<Passageiro> findByVooId(String vooId);
}
