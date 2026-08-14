package br.com.rodrigo.consultorio_medico_api.dto.historico_medico;

public record HistoricoMedicoRequest(
        String diagnostico,
        String prescricao,
        String observacoes
) {}