package br.com.rodrigo.consultorio_medico_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF para simplificar (em produção, configure adequadamente)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/recepcionista/cadastrar").permitAll() // Permite acesso público ao cadastro
                        .anyRequest().authenticated() // Todas outras rotas requerem autenticação
                )
                .formLogin(form -> form.disable()) // Desabilita login por formulário se for API REST
                .httpBasic(Customizer.withDefaults()); // Ou configure JWT/OAuth2 conforme sua necessidade

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
