package br.com.rodrigo.consultorio_medico_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "exames_medicos")
public class ExameMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "historico_medico_id", referencedColumnName = "id")
    private HistoricoMedico historicoMedico;

    @NotNull
    private String tipoExame;

    @NotNull
    private String resultado;

    @NotNull
    private LocalDateTime dataExame;

    private String arquivoUrl;
}
