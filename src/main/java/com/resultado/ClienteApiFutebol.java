package com.resultado;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClienteApiFutebol {

    private static final String API_KEY = System.getenv("API_RAPID_KEY");
    private static final String API_HOST = "api-football-v1.p.rapidapi.com";

    public static void main(String[] args) {
        // Exemplo - substitua por times vindos do ClienteApi
        String nomeTimeCasa = "Udinese";
        String nomeTimeFora = "Bologna";

        buscarPredicaoEntreTimes(nomeTimeCasa, nomeTimeFora);
    }

    public static void buscarPredicaoEntreTimes(String nomeTimeCasa, String nomeTimeFora) {
        Integer idTimeCasa = buscarIdTimePorNome(nomeTimeCasa);
        Integer idTimeFora = buscarIdTimePorNome(nomeTimeFora);

        if (idTimeCasa == null || idTimeFora == null) {
            System.out.println("Erro: Não foi possível encontrar os IDs dos times.");
            return;
        }

        buscarConfrontosDiretos(idTimeCasa, idTimeFora);
    }

    public static Integer buscarIdTimePorNome(String nomeTime) {
        String url = "https://api-football-v1.p.rapidapi.com/v3/teams?search=" + nomeTime.replace(" ", "%20");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-RapidAPI-Key", API_KEY)
                .header("X-RapidAPI-Host", API_HOST)
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray responseArray = jsonResponse.getAsJsonArray("response");

                if (responseArray.size() > 0) {
                    JsonObject time = responseArray.get(0).getAsJsonObject().getAsJsonObject("team");
                    int id = time.get("id").getAsInt();
                    System.out.println("Time encontrado: " + time.get("name").getAsString() + " | ID: " + id);
                    return id;
                } else {
                    System.out.println("Nenhum time encontrado para: " + nomeTime);
                    return null;
                }
            } else {
                System.out.println("Erro na requisição de ID: Código " + response.statusCode());
                return null;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void buscarConfrontosDiretos(int idCasa, int idFora) {
        String url = "https://api-football-v1.p.rapidapi.com/v3/fixtures/headtohead?h2h=" + idCasa + "-" + idFora;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-RapidAPI-Key", API_KEY)
                .header("X-RapidAPI-Host", API_HOST)
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray confrontos = jsonResponse.getAsJsonArray("response");

                int vitoriasCasa = 0;
                int vitoriasFora = 0;
                int empates = 0;
                int jogosAnalisados = 0;

                LocalDate hoje = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                for (int i = 0; i < confrontos.size(); i++) {
                    JsonObject partida = confrontos.get(i).getAsJsonObject();
                    JsonObject fixture = partida.getAsJsonObject("fixture");

                    String data = fixture.get("date").getAsString().substring(0, 10);
                    LocalDate dataPartida = LocalDate.parse(data, formatter);

                    if (dataPartida.isBefore(hoje.minusYears(10))) {
                        continue; // Ignorar jogos com mais de 10 anos
                    }

                    JsonObject teams = partida.getAsJsonObject("teams");
                    Boolean casaVencedor = teams.getAsJsonObject("home").get("winner").isJsonNull() ? null : teams.getAsJsonObject("home").get("winner").getAsBoolean();
                    Boolean foraVencedor = teams.getAsJsonObject("away").get("winner").isJsonNull() ? null : teams.getAsJsonObject("away").get("winner").getAsBoolean();

                    if (Boolean.TRUE.equals(casaVencedor)) {
                        vitoriasCasa++;
                    } else if (Boolean.TRUE.equals(foraVencedor)) {
                        vitoriasFora++;
                    } else {
                        empates++;
                    }

                    jogosAnalisados++;
                }

                System.out.println("\n🔎 Confrontos analisados nos últimos 10 anos: " + jogosAnalisados);
                System.out.println("🏠 Vitórias do time da casa: " + vitoriasCasa);
                System.out.println("🤝 Empates: " + empates);
                System.out.println("🛫 Vitórias do time visitante: " + vitoriasFora);

                if (jogosAnalisados > 0) {
                    double pctCasa = (vitoriasCasa * 100.0) / jogosAnalisados;
                    double pctEmpate = (empates * 100.0) / jogosAnalisados;
                    double pctFora = (vitoriasFora * 100.0) / jogosAnalisados;

                    System.out.printf("\n🔮 Predição:%n");
                    System.out.printf("- Casa vence: %.2f%%%n", pctCasa);
                    System.out.printf("- Empate: %.2f%%%n", pctEmpate);
                    System.out.printf("- Visitante vence: %.2f%%%n", pctFora);
                } else {
                    System.out.println("Nenhum confronto recente encontrado.");
                }

            } else {
                System.out.println("Erro na requisição de confrontos: Código " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}