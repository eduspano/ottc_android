package com.scipopulis.ottc.api.object;

import com.google.gson.annotations.Expose;

/**
 * Created by rafael on 07/10/16.
 */
public class Solicitante {
    @Expose
    private String cpf;
    @Expose
    private String email;
    @Expose
    private String nome;
    @Expose
    private Telefone telefone;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public void setTelefone(Telefone telefone) {
        this.telefone = telefone;
    }
}
