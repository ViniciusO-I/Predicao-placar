package com.resultado;

import com.google.gson.annotations.SerializedName;

public class Partida {

    @SerializedName("homeTeam")
    private Time timeCasa;

    @SerializedName("awayTeam")
    private Time timeFora;

    private Placar placar;
    private String status;

    public Time getTimeCasa() {
        return timeCasa;
    }

    public Time getTimeFora() {
        return timeFora;
    }

    public Placar getPlacar() {
        return placar;
    }

    public String getStatus() {
        return status;
    }
}