package com.scipopulis.ottc.api.object;

import com.google.gson.annotations.Expose;

/**
 * Created by rafael on 07/10/16.
 */
public class Telefone {
    @Expose
    private int ddd;
    @Expose
    private int numero;
    @Expose
    private boolean preferencial;
    @Expose
    private int ramal;
    @Expose
    private boolean sms;
    @Expose
    private String tipo;

    public int getDdd() {
        return ddd;
    }

    public void setDdd(int ddd) {
        this.ddd = ddd;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public boolean isPreferencial() {
        return preferencial;
    }

    public void setPreferencial(boolean preferencial) {
        this.preferencial = preferencial;
    }

    public int getRamal() {
        return ramal;
    }

    public void setRamal(int ramal) {
        this.ramal = ramal;
    }

    public boolean isSms() {
        return sms;
    }

    public void setSms(boolean sms) {
        this.sms = sms;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
