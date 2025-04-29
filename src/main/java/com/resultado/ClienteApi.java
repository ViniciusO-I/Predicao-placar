package com.resultado;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ClienteApi {

    private static  String URL_API = "https://api.football-data.org/v4/matches"; 
    private static  String CHAVE_API = System.getenv("API_FOOTBALL_KEY");; 

    public static void main(String[] args) {
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(URL_API))
                .header("X-Auth-Token", CHAVE_API)
                .GET()
                .build();

        try {
            HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());

            System.out.println("Código de Status: " + resposta.statusCode());

            if (resposta.statusCode() == 200) {
                Gson gson = new Gson();
                JsonObject corpoResposta = gson.fromJson(resposta.body(), JsonObject.class);
                
                JsonArray arrayPartidas = corpoResposta.getAsJsonArray("matches");
                
                for (int i = 0; i < arrayPartidas.size(); i++) {
                    Partida partida = gson.fromJson(arrayPartidas.get(i), Partida.class);
                    System.out.println("Partida: " + partida.getTimeCasa().getNome() + " x " + partida.getTimeFora().getNome());
                    
                    if (partida.getPlacar() != null && partida.getPlacar().getTempoIntegral() != null) {
                        System.out.println("Placar: " + partida.getPlacar().getTempoIntegral().getCasa() + " - " + partida.getPlacar().getTempoIntegral().getFora());
                    }
                
                    System.out.println("Status: " + TradutorStatus.traduzir(partida.getStatus()));
                    System.out.println("---------------------------------------");
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}