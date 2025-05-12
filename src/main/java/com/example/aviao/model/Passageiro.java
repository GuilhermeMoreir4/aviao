package com.example.aviao.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@Document(collection = "passageiros")
public class Passageiro {

    @Id
    private String id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos numéricos")
    private String cpf;

    @NotBlank(message = "O ID do voo é obrigatório")
    private String vooId;

    @Pattern(
            regexp = "pendente|realizado",
            message = "O status do check-in deve ser 'pendente' ou 'realizado'"
    )
    private String statusCheckin;
}
