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
import java.util.*;

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
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

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
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(nn);
        if (gamePlayer.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No existe el gamePlayer"), HttpStatus.UNAUTHORIZED);
        }
        else {
            if (gamePlayer.get().getPlayer().getId() != playerRepository.findByUserName(authentication.getName()).getId()) {
                return new ResponseEntity<>(makeMap("error", "No hacer trampa, programador pilluelo"), HttpStatus.UNAUTHORIZED);
            }
            else {
                return new ResponseEntity<>(gamePlayer.get().makeGameViewDTO(), HttpStatus.ACCEPTED);
            }
        }
    }

    @RequestMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String,Object>> getShips(@PathVariable Long gamePlayerId, Authentication authentication) {
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Inicie sesión para continuar"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No existe el gamePlayer"), HttpStatus.UNAUTHORIZED);
        }
        if (playerRepository.findByUserName(authentication.getName()).getGamePlayers().stream().noneMatch(gp -> gp.equals(gamePlayer.get()))) {
            return new ResponseEntity<>(makeMap("error", "Al player no le corresponde al gamePlayer"), HttpStatus.UNAUTHORIZED);
        }
        if (shipRepository.findByGamePlayer(gamePlayer.get()).isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No ubicaste los barcos"), HttpStatus.FORBIDDEN);
        }
        if (gamePlayer.get().getShips().size() == 0) {
            return new ResponseEntity<>(makeMap("error", "No colocaste todos los barcos"), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(makeMap("ships", gamePlayer.get().getShips().stream().map(Ship::makeShipDTO).collect(toList())), HttpStatus.ACCEPTED);
    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String,Object>> saveShips(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody List<Ship> ships) {
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Inicie sesión para continuar"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No existe el gamePlayer"), HttpStatus.UNAUTHORIZED);
        }
        if (playerRepository.findByUserName(authentication.getName()).getGamePlayers().stream().noneMatch(gp -> gp.equals(gamePlayer.get()))) {
            return new ResponseEntity<>(makeMap("error", "Al player no le corresponde al gamePlayer"), HttpStatus.UNAUTHORIZED);
        }
        if (ships.size() != 5) {
            return new ResponseEntity<>(makeMap("error", "Se necesitan 5 barcos para jugar"), HttpStatus.FORBIDDEN);
        }
        if (gamePlayer.get().getShips().size() != 0) {
            return new ResponseEntity<>(makeMap("error", "Ya colocaste todos los barcos"), HttpStatus.FORBIDDEN);
        }
        for (Ship newShip : ships) {
            if (newShip.getType().equals("carrier") && newShip.getShipLocations().size() != 5) {
                return new ResponseEntity<>(makeMap("error", "Carrier debe ocupar 5 lugares"), HttpStatus.FORBIDDEN);
            }
            if (newShip.getType().equals("battleship") && newShip.getShipLocations().size() != 4) {
                return new ResponseEntity<>(makeMap("error", "Battleship debe ocupar 4 lugares"), HttpStatus.FORBIDDEN);
            }
            if (newShip.getType().equals("submarine") && newShip.getShipLocations().size() != 3) {
                return new ResponseEntity<>(makeMap("error", "Submarine debe ocupar 3 lugares"), HttpStatus.FORBIDDEN);
            }
            if (newShip.getType().equals("destroyer") && newShip.getShipLocations().size() != 3) {
                return new ResponseEntity<>(makeMap("error", "Destroyer debe ocupar 3 lugares"), HttpStatus.FORBIDDEN);
            }
            if (newShip.getType().equals("patrolboat") && newShip.getShipLocations().size() != 2) {
                return new ResponseEntity<>(makeMap("error", "Patrol Boat debe ocupar 2 lugares"), HttpStatus.FORBIDDEN);
            }
        }
        for (Ship newShip : ships) {
            newShip.setGamePlayer(gamePlayer.get());
            shipRepository.save(newShip);
        }
        return new ResponseEntity<>(makeMap("OK", "Barcos colocados"), HttpStatus.CREATED);
    }

    @RequestMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String,Object>> getSalvo(@PathVariable Long gamePlayerId, Authentication authentication) {
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Inicie sesión para continuar"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No existe el gamePlayer"), HttpStatus.UNAUTHORIZED);
        }
        if (playerRepository.findByUserName(authentication.getName()).getGamePlayers().stream().noneMatch(gp -> gp.equals(gamePlayer.get()))) {
            return new ResponseEntity<>(makeMap("error", "Al player no le corresponde al gamePlayer"), HttpStatus.UNAUTHORIZED);
        }
        if (salvoRepository.findByGamePlayer(gamePlayer.get()).isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No ubicaste los disparos"), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(makeMap("salvoes", gamePlayer.get().getSalvoes().stream().map(Salvo::makeSalvoDTO).collect(toList())), HttpStatus.ACCEPTED);
    }

    @PostMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String,Object>> saveSalvo(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody Salvo salvos) {
        Optional<GamePlayer> gamePlayer1 = gamePlayerRepository.findById(gamePlayerId);
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Inicie sesión para continuar"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer1.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No existe el gamePlayer"), HttpStatus.UNAUTHORIZED);
        }
        Optional<GamePlayer> gamePlayer2 = gamePlayer1.get().getGame().getGamePlayers().stream().filter(gp -> !gp.equals(gamePlayer1.get())).findFirst();
        if (gamePlayer2.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No existe el gamePlayer del oponente"), HttpStatus.UNAUTHORIZED);
        }
        if ((playerRepository.findByUserName(authentication.getName()).getGamePlayers().stream().noneMatch(gp -> gp.equals(gamePlayer1.get())))) {
            return new ResponseEntity<>(makeMap("error", "Al player no le corresponde al gamePlayer"), HttpStatus.UNAUTHORIZED);
        }
        if ((salvos.getSalvoLocations().size() < 1 || salvos.getSalvoLocations().size() > 5)) {
            return new ResponseEntity<>(makeMap("error", "Deben haber entre 1 y 5 tiros por turno"), HttpStatus.FORBIDDEN);
        }
        if (gamePlayer1.get().getShips().size() != 5) {
            return new ResponseEntity<>(makeMap("error", "Debes colocar barcos para colocar tiros"), HttpStatus.FORBIDDEN);
        }
        if (gamePlayer2.get().getShips().size() != 5) {
            return new ResponseEntity<>(makeMap("error", "Debes esperar hasta que se coloquen los barcos para colocar tiros"), HttpStatus.FORBIDDEN);
        }
        if (gamePlayer1.get().getId() < gamePlayer2.get().getId()) {
            if (gamePlayer1.get().getSalvoes().size() == gamePlayer2.get().getSalvoes().size()) {
                salvos.setGamePlayer(gamePlayer1.get());
                salvos.setTurn(gamePlayer1.get().getSalvoes().size()+1);
                salvoRepository.save(salvos);
                return new ResponseEntity<>(makeMap("OK", "Misiles lanzados"), HttpStatus.CREATED);
            }
            else {
                return new ResponseEntity<>(makeMap("error", "Debe esperar al contrincante para volver a tirar"), HttpStatus.FORBIDDEN);
            }
        }
        else {
            if (gamePlayer1.get().getSalvoes().size() < gamePlayer2.get().getSalvoes().size()) {
                salvos.setGamePlayer(gamePlayer1.get());
                salvos.setTurn(gamePlayer1.get().getSalvoes().size()+1);
                salvoRepository.save(salvos);
                return new ResponseEntity<>(makeMap("OK", "Misiles lanzados"), HttpStatus.CREATED);
            }
            else {
                return new ResponseEntity<>(makeMap("error", "Debe esperar al contrincante para volver a tirar"), HttpStatus.FORBIDDEN);
            }
        }
    }

    @PostMapping("/players")
    //@RequestMapping(path = "/players", method = RequestMethod.POST)
    //RequestMapping es por default un GET
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String email, @RequestParam String password) {
        if (email.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No se ingresó nombre del usuario"), HttpStatus.FORBIDDEN);
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