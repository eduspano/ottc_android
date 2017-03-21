package com.scipopulis.ottc.api.object;

/**
 * Created by rafael on 06/10/16.
 */
public class TripList {

    private int index;

    private String corridaId;

    private String error = "";

    public String getError() {
        return error;
    }

    public String getCorridaId() {
        return corridaId;
    }

    public void setCorridaId(String corridaId) {
        this.corridaId = corridaId;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
