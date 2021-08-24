package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {		//Escribir ejemplos
			Player player1 = new Player("tomas.bianchi.02@gmail.com");
			playerRepository.save(player1);
			Player player2 = new Player("tbianchi@pioix.edu.ar");
			playerRepository.save(player2);
			Player player3 = new Player("lionelmessi@gmail.com");
			playerRepository.save(player3);
			Player player4 = new Player("lmessi@pioix.edu.ar");
			playerRepository.save(player4);
			//También se puede guardar como playerRepository.save(new Player("..."));

			Game game1 = new Game(LocalDateTime.now());
			gameRepository.save(game1);
			Game game2 = new Game(LocalDateTime.now().plusHours(1));
			gameRepository.save(game2);
			Game game3 = new Game(LocalDateTime.now().plusHours(2));
			gameRepository.save(game3);
			Game game4 = new Game(LocalDateTime.now().plusHours(3));
			gameRepository.save(game4);
			Game game5 = new Game(LocalDateTime.now().plusHours(4));
			gameRepository.save(game5);
			Game game6 = new Game(LocalDateTime.now().plusHours(5));
			gameRepository.save(game6);

			GamePlayer gamePlayer1 = new GamePlayer(game1, player1, LocalDateTime.now());
			gamePlayerRepository.save(gamePlayer1);
			GamePlayer gamePlayer2 = new GamePlayer(game1, player2, LocalDateTime.now());
			gamePlayerRepository.save(gamePlayer2);
			GamePlayer gamePlayer3 = new GamePlayer(game2, player3, LocalDateTime.now().plusHours(1));
			gamePlayerRepository.save(gamePlayer3);
			GamePlayer gamePlayer4 = new GamePlayer(game2, player4, LocalDateTime.now().plusHours(1));
			gamePlayerRepository.save(gamePlayer4);
			GamePlayer gamePlayer5 = new GamePlayer(game3, player1, LocalDateTime.now().plusHours(2));
			gamePlayerRepository.save(gamePlayer5);
			GamePlayer gamePlayer6 = new GamePlayer(game3, player3, LocalDateTime.now().plusHours(2));
			gamePlayerRepository.save(gamePlayer6);
			GamePlayer gamePlayer7 = new GamePlayer(game4, player2, LocalDateTime.now().plusHours(3));
			gamePlayerRepository.save(gamePlayer7);
			GamePlayer gamePlayer8 = new GamePlayer(game4, player4, LocalDateTime.now().plusHours(3));
			gamePlayerRepository.save(gamePlayer8);
			GamePlayer gamePlayer9 = new GamePlayer(game5, player1, LocalDateTime.now().plusHours(4));
			gamePlayerRepository.save(gamePlayer9);
			GamePlayer gamePlayer10 = new GamePlayer(game5, player4, LocalDateTime.now().plusHours(4));
			gamePlayerRepository.save(gamePlayer10);
			GamePlayer gamePlayer11 = new GamePlayer(game6, player2, LocalDateTime.now().plusHours(5));
			gamePlayerRepository.save(gamePlayer11);

			Ship ship1 = new Ship("Carrier", Arrays.asList("B5", "B6", "B7", "B8", "B9"), gamePlayer1);
			shipRepository.save(ship1);
			Ship ship2 = new Ship("Destroyer", Arrays.asList("G3", "J3", "I3"), gamePlayer1);
			shipRepository.save(ship2);
			Ship ship3 = new Ship("Patrol Boat", Arrays.asList("A1", "A2"), gamePlayer1);
			shipRepository.save(ship3);
			Ship ship4 = new Ship("Submarine", Arrays.asList("B8", "C8", "D8"), gamePlayer1);
			shipRepository.save(ship4);
			Ship ship5 = new Ship("Battleship", Arrays.asList("G3", "G4", "G5", "G6"), gamePlayer1);
			shipRepository.save(ship5);
			Ship ship6 = new Ship("Carrier", Arrays.asList("A1", "A2", "A3", "A4", "A5"), gamePlayer2);
			shipRepository.save(ship6);
			Ship ship7 = new Ship("Destroyer", Arrays.asList("C6", "C7", "C8"), gamePlayer2);
			shipRepository.save(ship7);
			Ship ship8 = new Ship("Patrol Boat", Arrays.asList("G1", "H1"), gamePlayer2);
			shipRepository.save(ship8);
			Ship ship9 = new Ship("Submarine", Arrays.asList("D6", "E6", "F6"), gamePlayer2);
			shipRepository.save(ship9);
			Ship ship10 = new Ship("Battleship", Arrays.asList("J5", "J6", "J7", "J8"), gamePlayer2);
			shipRepository.save(ship10);
			//Agregué 5 barcos para los primeros dos juegadores del juego 1

			Salvo salvo1 = new Salvo(1, Arrays.asList("B5", "H9"), gamePlayer1);
			salvoRepository.save(salvo1);
			Salvo salvo2 = new Salvo(1, Arrays.asList("A1", "J10"), gamePlayer2);
			salvoRepository.save(salvo2);
			Salvo salvo3 = new Salvo(2, Arrays.asList("D3", "D4"), gamePlayer1);
			salvoRepository.save(salvo3);
			Salvo salvo4 = new Salvo(2, Arrays.asList("C4", "F1"), gamePlayer2);
			salvoRepository.save(salvo4);
			Salvo salvo5 = new Salvo(3, Arrays.asList("F6", "A7"), gamePlayer1);
			salvoRepository.save(salvo5);
			Salvo salvo6 = new Salvo(3, Arrays.asList("H2", "E8"), gamePlayer2);
			salvoRepository.save(salvo6);
			//Agregué 3 turnos para el juego 1 y elegí 2 tiros por cada uno

			Score score1 = new Score(0f, LocalDateTime.now(), player1, game1);
			scoreRepository.save(score1);
			Score score2 = new Score(1f, LocalDateTime.now(), player2, game1);
			scoreRepository.save(score2);
			Score score3 = new Score(0.5f, LocalDateTime.now(), player1, game1);
			scoreRepository.save(score3);
			Score score4 = new Score(0.5f, LocalDateTime.now(), player2, game1);
			scoreRepository.save(score4);
		};
	}
}