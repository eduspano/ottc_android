package com.scipopulis.ottc.api.object;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by rafael on 29/09/16.
 */
public class Operator {

    private ArrayList<String> opers;

    private String error = "";


    public ArrayList<String> getOpers() {
        return opers;
    }

    public void setOpers(ArrayList<String> opers) {
        this.opers = opers;
    }


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
