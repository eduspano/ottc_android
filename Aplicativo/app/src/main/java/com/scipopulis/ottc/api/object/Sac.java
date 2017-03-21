package com.scipopulis.ottc.api.object;

import com.google.gson.annotations.Expose;

/**
 * Created by rafael on 07/10/16.
 */
public class Sac {
    @Expose
    private int assunto;
    @Expose
    private String descricao;
    @Expose
    private Endereco endereco;
    @Expose
    private Solicitante solicitante;

    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    public int getAssunto() {
        return assunto;
    }

    public void setAssunto(int assunto) {
        this.assunto = assunto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Solicitante getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Solicitante solicitante) {
        this.solicitante = solicitante;
    }
}
