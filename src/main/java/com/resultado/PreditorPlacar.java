package com.resultado;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PreditorPlacar {

    private static final String API_KEY_RAPIDAPI = System.getenv("API_RAPID_KEY");
    private static final String API_HOST_RAPIDAPI = "api-football-v1.p.rapidapi.com";

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {

        List<Jogo> jogos = buscarJogosDoBrasileirao();

        if (jogos.isEmpty()) {
            System.out.println("Nenhum jogo encontrado para o Brasileirão 2025.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("\n Selecione um jogo do Campeonato Brasileiro 2025 para fazer a predição:");
        for (int i = 0; i < jogos.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + jogos.get(i).getTimeCasa() + " x " + jogos.get(i).getTimeFora());
        }
        System.out.print("\n Digite o número do jogo: ");
        int opcao = scanner.nextInt();
        scanner.close();

        if (opcao < 1 || opcao > jogos.size()) {
            System.out.println("Opção inválida!");
            return;
        }

        Jogo jogoSelecionado = jogos.get(opcao - 1);

        String nomeNormalizadoCasa = normalizarNomeTime(jogoSelecionado.getTimeCasa());
        String nomeNormalizadoFora = normalizarNomeTime(jogoSelecionado.getTimeFora());

        System.out.println("\n Buscando IDs dos times no Brasileirão 2025...");
        Integer idTimeCasa = buscarIdTimeNoBrasileirao2025(nomeNormalizadoCasa);
        Integer idTimeFora = buscarIdTimeNoBrasileirao2025(nomeNormalizadoFora);

        if (idTimeCasa == null || idTimeFora == null) {
            System.out.println("Não foi possível encontrar os IDs dos times.");
            return;
        }

        System.out.println("\n Calculando confrontos diretos...");
        buscarConfrontosDiretos(idTimeCasa, idTimeFora);
    }

    public static List<Jogo> buscarJogosDoBrasileirao() {
        List<Jogo> jogos = new ArrayList<>();
        String url = "https://api-football-v1.p.rapidapi.com/v3/fixtures?league=71&season=2025";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-RapidAPI-Key", API_KEY_RAPIDAPI)
                .header("X-RapidAPI-Host", API_HOST_RAPIDAPI)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject corpoResposta = gson.fromJson(response.body(), JsonObject.class);
                JsonArray fixtures = corpoResposta.getAsJsonArray("response");

                for (int i = 0; i < fixtures.size(); i++) {
                    JsonObject fixture = fixtures.get(i).getAsJsonObject();
                    JsonObject times = fixture.getAsJsonObject("teams");

                    String timeCasa = times.getAsJsonObject("home").get("name").getAsString();
                    String timeFora = times.getAsJsonObject("away").get("name").getAsString();
                    String status = fixture.getAsJsonObject("fixture").getAsJsonObject("status").get("short").getAsString();

                    if (!status.equalsIgnoreCase("CANC") && !status.equalsIgnoreCase("PST")) {
                        jogos.add(new Jogo(timeCasa, timeFora));
                    }
                }
            } else {
                System.out.println("Erro ao buscar jogos do Brasileirão: Código " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return jogos;
    }

    public static Integer buscarIdTimeNoBrasileirao2025(String nomeTime) {
        String url = "https://api-football-v1.p.rapidapi.com/v3/teams?league=71&season=2025";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-RapidAPI-Key", API_KEY_RAPIDAPI)
                .header("X-RapidAPI-Host", API_HOST_RAPIDAPI)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray responseArray = jsonResponse.getAsJsonArray("response");

                for (int i = 0; i < responseArray.size(); i++) {
                    JsonObject teamObj = responseArray.get(i).getAsJsonObject().getAsJsonObject("team");
                    String nomeApi = teamObj.get("name").getAsString();

                    if (normalizarNomeTime(nomeApi).equalsIgnoreCase(nomeTime)) {
                        int id = teamObj.get("id").getAsInt();
                        System.out.println(" Time encontrado: " + nomeApi + " | ID: " + id);
                        return id;
                    }
                }
            } else {
                System.out.println("Erro ao buscar times do Brasileirão: Código " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void buscarConfrontosDiretos(int idCasa, int idFora) {
        String url = "https://api-football-v1.p.rapidapi.com/v3/fixtures/headtohead?h2h=" + idCasa + "-" + idFora;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-RapidAPI-Key", API_KEY_RAPIDAPI)
                .header("X-RapidAPI-Host", API_HOST_RAPIDAPI)
                .GET()
                .build();

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
                        continue;
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

                System.out.println("\n Confrontos analisados nos últimos 10 anos: " + jogosAnalisados);
                System.out.println(" Vitórias do time da casa: " + vitoriasCasa);
                System.out.println(" Empates: " + empates);
                System.out.println(" Vitórias do time visitante: " + vitoriasFora);

                if (jogosAnalisados > 0) {
                    double pctCasa = (vitoriasCasa * 100.0) / jogosAnalisados;
                    double pctEmpate = (empates * 100.0) / jogosAnalisados;
                    double pctFora = (vitoriasFora * 100.0) / jogosAnalisados;

                    System.out.printf("\n Predição:%n");
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

    public static String normalizarNomeTime(String nome) {
        nome = nome.replaceAll(" Futebol Clube", "");
        nome = nome.replaceAll(" Football Club", "");
        nome = nome.replaceAll(" FC", "");
        nome = nome.replaceAll(" AC", "");
        nome = nome.replaceAll(" AFC", "");
        nome = nome.replaceAll(" \\d{4}", "");
        nome = nome.trim();
        return nome;
    }
}

