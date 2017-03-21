package com.scipopulis.ottc.api.object;


import com.google.gson.JsonObject;

/**
 * Created by rafael on 29/09/16.
 */
public class Reset {
    private JsonObject result;

    private String error = "";

    public JsonObject getResult() {
        return result;
    }

    public void setResult(JsonObject result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
