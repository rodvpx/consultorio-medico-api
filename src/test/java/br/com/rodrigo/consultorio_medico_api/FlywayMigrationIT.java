package br.com.rodrigo.consultorio_medico_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Valida as migrations reais contra PostgreSQL 17.
 *
 * <p>O contexto sobe com {@code ddl-auto: validate}, então o teste falha se o
 * schema criado pelo Flyway divergir das entidades JPA. Sem Docker, o teste é
 * ignorado e o restante da suíte continua usando H2.</p>
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true",
        "app.admin.bootstrap-enabled=false"
})
class FlywayMigrationIT {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17");

    @DynamicPropertySource
    static void configurarDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deveAplicarMigrationsEValidarSchemaContraAsEntidades() {
        // given / when
        List<String> versoesAplicadas = jdbcTemplate.queryForList(
                "SELECT version FROM flyway_schema_history WHERE success = TRUE ORDER BY installed_rank",
                String.class);

        Integer tabelasDoDominio = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name IN ('usuarios', 'pacientes', 'medicos', 'recepcionistas',
                                     'consultas', 'historico_medico', 'exames_medicos')
                """, Integer.class);

        // then
        assertThat(versoesAplicadas).containsExactly("1");
        assertThat(tabelasDoDominio).isEqualTo(7);
    }
}
