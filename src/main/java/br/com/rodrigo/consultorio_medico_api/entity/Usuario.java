package br.com.rodrigo.consultorio_medico_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Email(message = "Email inválido.")
    @Column(unique = true)
    private String email;

    @NotNull
    @Size(min = 5, message = "A senha deve ter no mínimo 5 caracteres.")
    private String senha;
    private String role;
}
