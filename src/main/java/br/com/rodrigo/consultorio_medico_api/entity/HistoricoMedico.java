package br.com.rodrigo.consultorio_medico_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "historico_medico")
public class HistoricoMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", referencedColumnName = "id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    private Medico medico;

    @ManyToOne
    @JoinColumn(name = "consulta_id", referencedColumnName = "id")
    private Consulta consulta;

    @NotNull
    private String diagnostico;

    private String prescricao;

    private String observacoes;

    @NotNull
    private LocalDateTime dataRegistro;

    @OneToMany(mappedBy = "historicoMedico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExameMedico> exames;  // Aqui está o relacionamento com ExameMedico

}

