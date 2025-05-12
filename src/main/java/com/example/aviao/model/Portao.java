package com.example.aviao.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Document(collection = "portoes")
public class Portao {

    @Id
    private String id;

    @Pattern(
            regexp = "^[A-Z]\\d{1,2}$",
            message = "O código do portão deve seguir o padrão (ex: A5, B10)"
    )
    @NotBlank(message = "O código do portão é obrigatório")
    private String codigo;

    // boolean não precisa de validação, pois é primitivo e terá valor padrão (false)
    private boolean disponivel;
}
