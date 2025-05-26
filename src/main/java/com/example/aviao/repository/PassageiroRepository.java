package com.example.aviao.repository;


import com.example.aviao.model.Passageiro;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PassageiroRepository extends MongoRepository<Passageiro, String>, CrudRepository<Passageiro, String> {
    Optional<Passageiro> findByCpf(String cpf);
    List<Passageiro> findByVooId(String vooId);

    @Override
    <S extends Passageiro> S save(S entity);
}
