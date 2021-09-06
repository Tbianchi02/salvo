package com.codeoftheweb.salvo;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping("/games")
    public Map<String,Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            dto.put("player", "Guest");
        }
        else {
            dto.put("player", playerRepository.findByUserName(authentication.getName()).makePlayerDTO());
        }
        dto.put("games", gameRepository.findAll().stream().map(game -> game.makeGameDTO()).collect(toList()));
        return dto;
    }

    @PostMapping("/games")
    public ResponseEntity<Map<String,Object>> createGames(Authentication authentication) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Inicie sesión para crear un juego"), HttpStatus.UNAUTHORIZED);
        }
        else {
            Game newGame = new Game(LocalDateTime.now());
            gameRepository.save(newGame);
            Player currentPlayer = playerRepository.findByUserName(authentication.getName());
            GamePlayer newGamePlayer = new GamePlayer(newGame, currentPlayer, LocalDateTime.now());
            gamePlayerRepository.save(newGamePlayer);
            return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
        }
    }

    @PostMapping("/game/{nn}/players")
    public ResponseEntity<Map<String,Object>> joinGame(@PathVariable Long nn, Authentication authentication) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Inicie sesión para crear un juego"), HttpStatus.UNAUTHORIZED);
        }
        else {
            if (gameRepository.findById(nn).isEmpty()) {
                return new ResponseEntity<>(makeMap("error", "No existe el juego"), HttpStatus.FORBIDDEN);
            }
            else {
                //if (gameRepository.findById(nn).get().getGamePlayers().stream().findFirst().get().getPlayer().getId() == playerRepository.findByUserName(authentication.getName()).getId()) {
                //Podría usar el findFirst ya que solo tengo dos elementos dentro del vector
                if (gameRepository.findById(nn).get().getGamePlayers().stream().anyMatch(player -> player.getPlayer().getUserName().equals(authentication.getName()))) {
                    return new ResponseEntity<>(makeMap("error", "Usted ya está jugando esta partida"), HttpStatus.FORBIDDEN);
                }
                else {
                    if (gameRepository.findById(nn).get().getGamePlayers().size() > 1) {
                        return new ResponseEntity<>(makeMap("error", "Juego lleno"), HttpStatus.FORBIDDEN);
                    }
                    else {
                        Game joinedGame = gameRepository.findById(nn).get();
                        Player currentPlayer = playerRepository.findByUserName(authentication.getName());
                        GamePlayer newGamePlayer = new GamePlayer(joinedGame, currentPlayer, LocalDateTime.now());
                        gamePlayerRepository.save(newGamePlayer);
                        return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
                    }
                }
            }
        }
    }

    @RequestMapping("/game_view/{nn}")
    public ResponseEntity<Map<String,Object>> findGamePlayer(@PathVariable Long nn, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).get();
        if (gamePlayer.getPlayer().getId() != playerRepository.findByUserName(authentication.getName()).getId()) {
            return new ResponseEntity<>(makeMap("error", "No hacer trampa, programador pilluelo"), HttpStatus.UNAUTHORIZED);
        }
        else {
            return new ResponseEntity<>(gamePlayer.makeGameViewDTO(), HttpStatus.ACCEPTED);
        }
    }

    @PostMapping("/players")
    //@RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String email, @RequestParam String password) {
        if (email.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No se ingresó nombre"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByUserName(email);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Usuario ya existente"), HttpStatus.FORBIDDEN);
        }
        Player newPlayer = playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap("id", newPlayer.getId()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}