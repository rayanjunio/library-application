package com.library.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public class UserUpdateDTO {
    @Size(min = 2, max = 70, message = "O nome deve ter entre 2 e 70 caracteres")
    private String name;

    @Email(message = "O formato do e-mail é inválido")
    private String email;

    @CPF(message = "O CPF informado é inválido")
    private String cpf;

    public UserUpdateDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
} 