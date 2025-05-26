package com.example.aviao.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.*;

@Document(collection = "funcionarios")
@Data
public class Funcionario {
    @Id
    private String id;

    @NotBlank
    private String nome;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String senha; // Será armazenada criptografada

    @NotBlank
    private String cargo; // Ex: "admin" ou "comum"
}
