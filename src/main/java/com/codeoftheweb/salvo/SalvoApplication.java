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
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository) {
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
			Ship ship2 = new Ship("Cruiser", Arrays.asList("F3", "G3", "H3"), gamePlayer2);
			shipRepository.save(ship2);
			Ship ship3 = new Ship("Destroyer", Arrays.asList("A1", "A2"), gamePlayer3);
			shipRepository.save(ship3);
			Ship ship4 = new Ship("Submarine", Arrays.asList("B8", "C8", "D8"), gamePlayer4);
			shipRepository.save(ship4);
			Ship ship5 = new Ship("Battleship", Arrays.asList("G3", "G4", "G5", "G6"), gamePlayer5);
			shipRepository.save(ship5);
			Ship ship6 = new Ship("Cruiser", Arrays.asList("E7", "F7", "G7"), gamePlayer6);
			shipRepository.save(ship6);
		};
	}
}