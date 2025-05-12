package com.example.aviao.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@Document(collection = "voos")
public class Voo {

    @Id
    private String id;

    @NotBlank(message = "O número do voo é obrigatório")
    @Pattern(
            regexp = "^[A-Z]{2}\\d{3,4}$",
            message = "O número do voo deve seguir o padrão (ex: AZ1234)"
    )
    private String numeroVoo;

    @NotBlank(message = "A origem é obrigatória")
    private String origem;

    @NotBlank(message = "O destino é obrigatório")
    private String destino;

    @NotNull(message = "A data e hora de partida são obrigatórias")
    private LocalDateTime dataHoraPartida;

    @NotBlank(message = "O ID do portão é obrigatório")
    private String portaoId;

    @Pattern(
            regexp = "programado|embarque|concluido",
            message = "O status deve ser 'programado', 'embarque' ou 'concluido'"
    )
    private String status;
}
