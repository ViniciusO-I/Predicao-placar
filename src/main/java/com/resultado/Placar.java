package com.resultado;

public class Placar {
    private ResultadoTempoIntegral tempoIntegral;

    public ResultadoTempoIntegral getTempoIntegral() {
        return tempoIntegral;
    }

    public static class ResultadoTempoIntegral {
        private Integer casa;
        private Integer fora;

        public Integer getCasa() {
            return casa;
        }

        public Integer getFora() {
            return fora;
        }
    }
}