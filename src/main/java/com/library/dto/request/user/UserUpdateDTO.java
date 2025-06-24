package com.library.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public class UserUpdateDTO {
    @Size(min = 2, max = 70, message = "Name must be between 2 and 70 characters")
    private String name;

    @Email(message = "E-mail format is invalid")
    private String email;

    @CPF(message = "CPF is invalid")
    private String cpf;

    public UserUpdateDTO() {}

    public UserUpdateDTO(String name, String email, String cpf) {
        this.name = name;
        this.email = email;
        this.cpf = cpf;
    }

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