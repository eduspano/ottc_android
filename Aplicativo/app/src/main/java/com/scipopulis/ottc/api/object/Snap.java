package com.scipopulis.ottc.api.object;

import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

/**
 * Created by rafael on 13/10/16.
 */
public class Snap {

    private JsonObject dados;

    private Bundle extras;

    public JsonObject getDados() {
        return dados;
    }

    public void setDados(JsonObject dados) {
        this.dados = dados;
    }

    public Bundle getExtras() {
        return extras;
    }

    public void setExtras(Bundle extras) {
        this.extras = extras;
    }
}
