package br.com.rodrigo.consultorio_medico_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "recepcionistas")
public class Recepcionista {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private Usuario usuario;

    @NotNull
    @Pattern(regexp = "\\d{11}", message = "CPF inválido. Deve conter 11 dígitos.")
    @Column(unique = true)
    private String cpf;

    @NotNull(message = "Nome é obrigatório.")
    private String nome;

    @Pattern(regexp = "\\(\\d{2}\\) \\d{5}-\\d{4}", message = "Formato de telefone inválido. Use (XX) XXXXX-XXXX.")
    private String telefone;

    @Past(message = "A data de contratação deve ser uma data no passado.")
    private LocalDate dataContratacao;

    private boolean status;


}

