package com.scipopulis.ottc.api.object;


import com.google.gson.JsonObject;
import com.scipopulis.ottc.model.User;

/**
 * Created by rafael on 29/09/16.
 */
public class Login {

    private User usuario = new User();

    private String error = "";

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(JsonObject response, String token) {
        String sexo = "";
        if( response.get("gender").toString().equals("masculino") ){
            sexo="masculino";
        }else{
            sexo="feminino";
        }
        this.usuario.setName( response.get("name").getAsString() );
        this.usuario.setGender( sexo );
        this.usuario.setCpf( response.get("cpf").getAsString() );
        this.usuario.setEmail( response.get("email").getAsString() );
        this.usuario.setToken( token );
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

}
