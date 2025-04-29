package com.resultado;

import com.google.gson.annotations.SerializedName;

public class Time {

    @SerializedName("name")
    private String nome;

    public String getNome() {
        return nome;
    }
}