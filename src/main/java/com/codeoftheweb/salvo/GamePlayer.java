package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Ship> ships;

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    @OrderBy
    Set<Salvo> salvoes;

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    public GamePlayer() { }

    public GamePlayer(Game game, Player player, LocalDateTime joinDate) {
        this.game = game;
        this.player = player;
        this.joinDate = joinDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Optional<Score> getScore() {
        return this.getPlayer().getScore(this.getGame());
    }

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().makePlayerDTO());
        return dto;
    }

    public Map<String, Object>makeGameViewDTO(){
        Map<String, Object> dto= new LinkedHashMap<>();
        dto.put("id", this.getGame().getId());
        dto.put("created", this.getJoinDate());
        dto.put("gameState", "PLACESHIPS");
        dto.put("gamePlayers", this.getGame().getGamePlayers().stream().map(gamePlayer-> gamePlayer.makeGamePlayerDTO()).collect(toList()));
        dto.put("ships", this.getShips().stream().map(ship -> ship.makeShipDTO()).collect(toList()));
        dto.put("salvoes", this.getGame().getGamePlayers().stream().flatMap(gamePlayer -> gamePlayer.getSalvoes().stream().map(salvo -> salvo.makeSalvoDTO())).collect(toList()));
        dto.put("hits", makeHitsDTO());
        return dto;
    }

    public Map<String, Object>makeHitsDTO(){
        Map<String, Object> dto= new LinkedHashMap<>();
        GamePlayer opponent = getOpponent();
        if(opponent == null){
            dto.put("self", new ArrayList<>());
            dto.put("opponent", new ArrayList<>());
        }
        else {
            dto.put("self", this.makeListHits());
            dto.put("opponent", opponent.makeListHits());
        }
        return dto;
    }

    public GamePlayer getOpponent(){
        GamePlayer opponent = this.getGame().getGamePlayers().stream().filter(gp -> !gp.getId().equals(this.getId())).findFirst().orElse(null);
        return opponent;
    }

    public List<Map<String, Object>>makeListHits(){
        List<Map<String, Object>> listHits= new ArrayList<>();
        Ship carrier = this.getShips().stream().filter(s -> s.getType().equals("carrier")).findFirst().get();
        List<String> carrierLocations = carrier.getShipLocations();
        Ship battleship = this.getShips().stream().filter(s -> s.getType().equals("battleship")).findFirst().get();
        List<String> battleshipLocations = battleship.getShipLocations();
        Ship submarine = this.getShips().stream().filter(s -> s.getType().equals("submarine")).findFirst().get();
        List<String> submarineLocations = submarine.getShipLocations();
        Ship destroyer = this.getShips().stream().filter(s -> s.getType().equals("destroyer")).findFirst().get();
        List<String> destroyerLocations = destroyer.getShipLocations();
        Ship patrolboat = this.getShips().stream().filter(s -> s.getType().equals("patrolboat")).findFirst().get();
        List<String> patrolboatLocations = patrolboat.getShipLocations();

        int carrierTotal = 0;
        int battleshipTotal = 0;
        int submarineTotal = 0;
        int destroyerTotal = 0;
        int patrolboatTotal = 0;

        for (Salvo salvoes : this.getOpponent().getSalvoes()) {
            Map<String, Object> hitsTurn= new LinkedHashMap<>();

            int carrierTurn = 0;
            int battleshipTurn = 0;
            int submarineTurn = 0;
            int destroyerTurn = 0;
            int patrolboatTurn = 0;
            int missed = salvoes.getSalvoLocations().size();

            List<String> hitLocations= new ArrayList<>();
            for (String salvoLocation : salvoes.getSalvoLocations()) {
                if (carrierLocations.contains(salvoLocation)) {
                    hitLocations.add(salvoLocation);
                    carrierTotal++;
                    carrierTurn++;
                    missed--;
                }
                if (battleshipLocations.contains(salvoLocation)) {
                    hitLocations.add(salvoLocation);
                    battleshipTotal++;
                    battleshipTurn++;
                    missed--;
                }
                if (submarineLocations.contains(salvoLocation)) {
                    hitLocations.add(salvoLocation);
                    submarineTotal++;
                    submarineTurn++;
                    missed--;
                }
                if (destroyerLocations.contains(salvoLocation)) {
                    hitLocations.add(salvoLocation);
                    destroyerTotal++;
                    destroyerTurn++;
                    missed--;
                }
                if (patrolboatLocations.contains(salvoLocation)) {
                    hitLocations.add(salvoLocation);
                    patrolboatTotal++;
                    patrolboatTurn++;
                    missed--;
                }
            }

            Map<String, Object> listDamages= new LinkedHashMap<>();
            listDamages.put("carrierHits", carrierTurn);
            listDamages.put("battleshipHits", battleshipTurn);
            listDamages.put("submarineHits", submarineTurn);
            listDamages.put("destroyerHits", destroyerTurn);
            listDamages.put("patrolboatHits", patrolboatTurn);
            listDamages.put("carrier", carrierTotal);
            listDamages.put("battleship", battleshipTotal);
            listDamages.put("submarine", submarineTotal);
            listDamages.put("destroyer", destroyerTotal);
            listDamages.put("patrolboat", patrolboatTotal);

            hitsTurn.put("turn", salvoes.getTurn());
            hitsTurn.put("hitLocations", hitLocations);
            hitsTurn.put("damages", listDamages);
            hitsTurn.put("missed", missed);

            listHits.add(hitsTurn);
        }
        return listHits;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}