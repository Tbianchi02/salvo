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

    @ElementCollection
    @Column(name="selfHits")
    private List<String> self = new ArrayList<>();

    @ElementCollection
    @Column(name="opponentHits")
    private List<String> opponent = new ArrayList<>();

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
        dto.put("hits", this.makeHitsDTO());
        return dto;
    }

    public Map<String, Object>makeHitsDTO(){
        Map<String, Object> dto= new LinkedHashMap<>();
        dto.put("self", self);
        dto.put("opponent", opponent);
        return dto;
    }
}