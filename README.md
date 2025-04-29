# Predição de Placar - Campeonato Brasileiro 2025 

Projeto em **Java** desenvolvido para **predizer o resultado de partidas** do **Campeonato Brasileiro 2025**.

Utiliza APIs de futebol para:
- Buscar partidas ativas
- Consultar histórico de confrontos entre os times
- Analisar vitórias, empates e derrotas
- Calcular a porcentagem de chance de cada resultado

---

## 📊 Tecnologias utilizadas

- Java 21
- Maven
- API-Football (via RapidAPI)
- Football-Data.org
- Git e GitHub

---

## 📅 Funcionalidades

- Buscar partidas da rodada atual do Brasileirão
- Selecionar uma partida para análise
- Buscar confrontos diretos históricos (head-to-head)
- Calcular:
  - % de vitória do time da casa
  - % de empate
  - % de vitória do time visitante
- Exibir o resultado de predição


---

## 📖 Como executar o projeto

### 1. Clone o repositório

```bash
git clone https://github.com/ViniciusO-I/Predicao-placar.git
```

### 2. Configure as variáveis de ambiente

Crie um arquivo `.env` com as variáveis:

```bash
API_FOOTBALL_KEY=seu_token_da_api_football
API_RAPID_KEY=seu_token_da_rapidapi
```

Ou defina diretamente no terminal antes de executar:

```bash
export API_FOOTBALL_KEY=seu_token
export API_RAPID_KEY=seu_token
```

### 3. Compile e execute

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.resultado.PreditorPlacar"
```


---



## 👨‍💻 Desenvolvedor

Vinícius Oliveira  
[GitHub](https://github.com/ViniciusO-I) |
[LinkedIn](www.linkedin.com/in/vinicius-rodrigues-tecnologia)

---

## 📈 Status do Projeto

Projeto em desenvolvimento ativo!  
Melhorias previstas:
- ⭐ Integração com banco de dados para salvar histórico
- ⭐ Dashboard web para exibir predições
- ⭐ Algoritmos de machine learning para aprimorar a predição


---

## 📉 License

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.





