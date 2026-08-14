package br.com.rodrigo.consultorio_medico_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    private Medico medico;

    @ManyToOne
    @JoinColumn(name = "paciente_id", referencedColumnName = "id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "recepcionista_id", referencedColumnName = "id")
    private Recepcionista recepcionista;

    @NotNull
    @FutureOrPresent(message = "A data da consulta não pode ser no passado.")
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusConsulta status;

    private String motivoCancelamento;

    private String observacoes;

    public enum StatusConsulta {
        AGENDADA,
        CANCELADA,
        REALIZADA,
        FINALIZADA
    }


}

