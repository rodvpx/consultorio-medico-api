package br.com.rodrigo.consultorio_medico_api.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornar404ParaRecursoNaoEncontrado() {
        // when
        ResponseEntity<ErrorResponse> resposta = handler.tratarRecursoNaoEncontrado(
                new RecursoNaoEncontradoException("Paciente não encontrado."));

        // then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Paciente não encontrado.");
    }

    @Test
    void deveRetornar409ParaConflito() {
        // when
        ResponseEntity<ErrorResponse> resposta = handler.tratarConflito(
                new ConflitoException("CPF já cadastrado."));

        // then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody().mensagem()).isEqualTo("CPF já cadastrado.");
    }

    @Test
    void deveRetornar409ParaViolacaoDeIntegridade() {
        // when
        ResponseEntity<ErrorResponse> resposta = handler.tratarIntegridade(
                new DataIntegrityViolationException("duplicado"));

        // then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody().mensagem())
                .isEqualTo("Os dados informados conflitam com um registro já existente.");
    }

    @Test
    void deveRetornar400ParaRegraDeNegocio() {
        // when
        ResponseEntity<ErrorResponse> resposta = handler.tratarRegraDeNegocio(
                new RegraDeNegocioException("Status obrigatório."));

        // then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Status obrigatório.");
    }

    @Test
    void deveRetornar400ComCamposParaErroDeValidacao() {
        // given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "cpf", "CPF inválido."));

        // when
        ResponseEntity<ErrorResponse> resposta = handler.tratarValidacao(
                new MethodArgumentNotValidException(null, bindingResult));

        // then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Dados inválidos.");
        assertThat(resposta.getBody().campos()).containsEntry("cpf", "CPF inválido.");
    }

    @Test
    void deveRetornar400ParaCorpoInvalido() {
        // when
        ResponseEntity<ErrorResponse> resposta = handler.tratarCorpoInvalido(
                new HttpMessageNotReadableException("json inválido", (org.springframework.http.HttpInputMessage) null));

        // then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody().mensagem()).startsWith("Corpo da requisição inválido.");
    }

    @Test
    void deveRetornar403ParaAcessoNegado() {
        // when
        ResponseEntity<ErrorResponse> resposta = handler.tratarAcessoNegado(
                new AccessDeniedException("negado"));

        // then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Acesso negado.");
    }

    @Test
    void deveRetornar400ParaParametroInvalido() {
        // when
        ResponseEntity<ErrorResponse> resposta = handler.tratarParametroInvalido(
                new MethodArgumentTypeMismatchException("abc", UUID.class, "id", null, null));

        // then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Parâmetro inválido: id.");
    }

    @Test
    void deveRetornar404ParaRotaInexistente() {
        // when
        ResponseEntity<ErrorResponse> resposta = handler.tratarRotaInexistente(
                new NoResourceFoundException(HttpMethod.GET, "/inexistente", "inexistente"));

        // then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Recurso não encontrado.");
    }

    @Test
    void deveRetornar405ParaMetodoNaoSuportado() {
        // when
        ResponseEntity<ErrorResponse> resposta = handler.tratarMetodoNaoSuportado(
                new HttpRequestMethodNotSupportedException("PATCH"));

        // then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Método HTTP não suportado para este recurso.");
    }

    @Test
    void deveRetornar500ParaErroInesperado() {
        // when
        ResponseEntity<ErrorResponse> resposta = handler.tratarErroInesperado(
                new RuntimeException("falha inesperada"));

        // then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Erro interno ao processar a requisição.");
    }
}
