package com.resultado;

public class TradutorStatus {

    public static String traduzir(String statusOriginal) {
        if (statusOriginal == null) {
            return "Desconhecido";
        }

        switch (statusOriginal) {
            case "IN_PLAY":
                return "Em andamento";
            case "PAUSED":
                return "Pausado";
            case "FINISHED":
                return "Finalizado";
            case "SCHEDULED":
                return "Agendado";
            case "POSTPONED":
                return "Adiado";
            default:
                return "Outro";
        }
    }
}