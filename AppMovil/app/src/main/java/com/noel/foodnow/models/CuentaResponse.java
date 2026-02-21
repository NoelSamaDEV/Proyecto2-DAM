package com.noel.foodnow.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CuentaResponse {

    @SerializedName("lineas")
    private List<LineaCuenta> lineas;

    @SerializedName("total")
    private Double total;

    // Getters
    public List<LineaCuenta> getLineas() {
        return lineas;
    }

    public Double getTotal() {
        return total;
    }
}